// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.saas.step4entitlementchange;

import com.example.saas.database.Customer;
import com.example.saas.database.Customer.Product;
import com.example.saas.database.JsonDatabase;
import com.example.saas.pubsub.ProcurementEventType;
import com.example.saas.pubsub.ProcurementPubsubMessage;
import com.example.saas.pubsub.ProcurementPubsubMessage.AccountMessage;
import com.example.saas.pubsub.ProcurementPubsubMessage.EntitlementMessage;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloudcommerceprocurement.v1.model.Account;
import com.google.cloudcommerceprocurement.v1.model.Approval;
import com.google.cloudcommerceprocurement.v1.model.Entitlement;
import com.google.gson.Gson;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.UUID;

/** Handles incoming Pub/Sub messages from the Cloud Commerce Procurement Service. */
public class ProcurementMessageReceiver implements MessageReceiver {

  private Gson gson;
  private JsonDatabase db;
  private ProcurementService service;

  public ProcurementMessageReceiver() throws GeneralSecurityException, IOException {
    gson = new Gson();
    db = new JsonDatabase();
    service = new ProcurementService();
  }

  @Override
  public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
    System.out.printf("Received message: %s\n", message.getData().toStringUtf8());

    boolean ack = false;
    try {
      ack = processPubsubMessage(parseMessage(message));
    } catch (IOException e) {
      throw new RuntimeException(
          String.format("Failed to handle message %s.", message.getMessageId()), e);
    }

    if (ack) {
      consumer.ack();
    }
  }

  /** Processes a Pub/Sub message from the Cloud Commerce Procurement Service. */
  public boolean processPubsubMessage(ProcurementPubsubMessage message) throws IOException {
    if (message == null) {
      return true;
    }

    if (message.getAccount() != null && !message.getAccount().getId().isEmpty()) {
      return processAccount(message.getAccount());
    }

    if (message.getEntitlement() != null && !message.getEntitlement().getId().isEmpty()) {
      return processEntitlement(message.getEntitlement(), message.getEventType());
    }

    return false;
  }

  /** Processes a Pub/Sub message for an Account resource. */
  public boolean processAccount(AccountMessage accountMessage) throws IOException {
    String accountId = accountMessage.getId();

    Customer customer = db.read(accountId);
    Account account = service.getAccount(accountId);

    ////////////////////////////////////////// IMPORTANT //////////////////////////////////////////
    // In true integrations, Pub/Sub messages for new accounts should be ignored. Account        //
    // approvals are granted as a one-off action during customer sign up. This codelab does not  //
    // include the sign up flow, so it chooses to approve accounts here instead. Production code //
    // for real, non-codelab services should never blidnly approve these. The following should   //
    // be done as a result of a user signing up.                                                 //
    ///////////////////////////////////////////////////////////////////////////////////////////////

    if (account != null) {
      Approval approval = getSignupApproval(account);

      if (approval != null) {
        if (approval.getState().equals("PENDING")) {
          // See note above. Actual production integrations should not approve blindly when
          // receiving a message.
          service.approveAccount(accountId);
        } else if (approval.getState().equals("APPROVED") && customer == null) {
          // Now that it's approved, store a new record in the database.
          createCustomer(accountId);
        }
      }

    } else {
      // The account has been deleted, so delete the database record.
      if (customer != null) {
        db.delete(accountId);
      }
    }

    // Always ack account message. We only care about the above scenarios.
    return true;
  }

  /** Processes a Pub/Sub message for an Entitlement resource. */
  public boolean processEntitlement(
      EntitlementMessage entitlementMessage, ProcurementEventType eventType) throws IOException {
    String entitlementId = entitlementMessage.getId();

    Entitlement entitlement = service.getEntitlement(entitlementId);

    if (entitlement == null) {
      // TODO: Complete in section 5.
      return false;
    }

    String accountId = ProcurementService.getAccountId(entitlement.getAccount());
    Customer customer = db.read(accountId);

    if (customer == null) {
      // If the record for this customer does not exist, don't ack the message and wait until an
      // account message is handled and a record is created.
      return false;
    }

    switch (eventType) {
      case ENTITLEMENT_CREATION_REQUESTED:
        if (entitlement.getState().equals("ENTITLEMENT_ACTIVATION_REQUESTED")) {
          service.approveEntitlement(entitlementId);
          return true;
        }
        break;
      case ENTITLEMENT_ACTIVE:
        if (entitlement.getState().equals("ENTITLEMENT_ACTIVE")) {
          updateCustomer(customer, entitlement);
          return true;
        }
        break;
      case ENTITLEMENT_PLAN_CHANGE_REQUESTED:
        if (entitlement.getState().equals("ENTITLEMENT_PENDING_PLAN_CHANGE_APPROVAL")) {
          service.approveEntitlementPlanChange(entitlementId, entitlement.getNewPendingPlan());
          return true;
        }
        break;
      case ENTITLEMENT_PLAN_CHANGED:
        if (entitlement.getState().equals("ENTITLEMENT_ACTIVE")) {
          updateCustomer(customer, entitlement);
          return true;
        }
        break;
      case ENTITLEMENT_PLAN_CHANGE_CANCELLED:
        // Do nothing. We approved the original change, but we never recorded it or changed the
        // service level since it hadn't taken effect yet.
        return true;
      case ENTITLEMENT_CANCELLED:
      case ENTITLEMENT_PENDING_CANCELLATION:
      case ENTITLEMENT_CANCELLATION_REVERTED:
      case ENTITLEMENT_DELETED:
        // TODO: Complete in section 5.
        return false;
    }

    return false;
  }

  // Creates a new customer in the database.
  private void createCustomer(String accountId) throws IOException {
    Customer customer = new Customer();
    customer.procurementAccountId = accountId;
    customer.internalAccountId = generateInternalAccountId();
    customer.products = new HashMap<>();

    db.write(accountId, customer);
  }

  // Updates an existing customer in the database for a new Entitlement.
  private void updateCustomer(Customer customer, Entitlement entitlement) throws IOException {
    Product product = new Product();
    product.productId = entitlement.getProduct();
    product.planId = entitlement.getPlan();
    product.startTime = entitlement.getCreateTime();

    String consumerId = entitlement.getUsageReportingId();
    if (consumerId != null && !consumerId.isEmpty()) {
      product.consumerId = consumerId;
    }

    customer.products.put(product.productId, product);
    db.write(customer.procurementAccountId, customer);
  }

  // Parses a Pub/Sub message into one matching the Procurement data format.
  private ProcurementPubsubMessage parseMessage(PubsubMessage message) {
    String data = message.getData().toStringUtf8();

    if (data != null && !data.isEmpty()) {
      return gson.fromJson(data, ProcurementPubsubMessage.class);
    }
    return null;
  }

  // Generates an internal customer ID for the provider's system.
  private static String generateInternalAccountId() {
    // TODO: Replace with whatever ID generation code already exists.
    return UUID.randomUUID().toString();
  }

  // Finds the 'signup' approval on an Account resource.
  private static Approval getSignupApproval(Account account) {
    for (Approval approval : account.getApprovals()) {
      if (approval.getName().equals("signup")) {
        return approval;
      }
    }
    return null;
  }
}
