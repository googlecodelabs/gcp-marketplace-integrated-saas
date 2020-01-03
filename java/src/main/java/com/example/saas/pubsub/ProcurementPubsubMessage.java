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

package com.example.saas.pubsub;

/** POJO for Procurement Cloud Pub/Sub messages. */
public class ProcurementPubsubMessage {
  private ProcurementEventType eventType;
  private AccountMessage account;
  private EntitlementMessage entitlement;

  /** Sets the event type. */
  public void setEventType(ProcurementEventType eventType) {
      this.eventType = eventType;
  }

  /** Gets the event type. */
  public ProcurementEventType getEventType() {
    return eventType;
  }

  /** Sets the Account message. */
  public void setAccount(AccountMessage account) {
    this.account = account;
  }

  /** Gets the Account message. */
  public AccountMessage getAccount() {
    return account;
  }

  /** Sets the Entitlement message. */
  public void setEntitlement(EntitlementMessage entitlement) {
    this.entitlement = entitlement;
  }

  /** Gets the Entitlement message. */
  public EntitlementMessage getEntitlement() {
    return entitlement;
  }

  /** POJO for Procurement information about the Account resource from Cloud Pub/Sub. */
  public static class AccountMessage {
    private String id;

    /** Sets the Account ID. */
    public void setId(String id) {
      this.id = id;
    }

    /** Gets the Account ID. */
    public String getId() {
      return id;
    }
  }

  /** POJO for Procurement information about the Entitlement resource from Cloud Pub/Sub. */
  public static class EntitlementMessage {
    private String id;
    private String newPlan;

    /** Sets the Entitlement ID. */
    public void setId(String id) {
      this.id = id;
    }

    /** Gets the Entitlement ID. */
    public String getId() {
      return id;
    }

    /** Sets the new plan. */
    public void setNewPlan(String newPlan) {
      this.newPlan = newPlan;
    }

    /** Gets the new plan. */
    public String getNewPlan() {
      return newPlan;
    }
  }
}
