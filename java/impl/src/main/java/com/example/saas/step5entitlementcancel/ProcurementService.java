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

package com.example.saas.step5entitlementcancel;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloudcommerceprocurement.v1.CloudCommercePartnerProcurementService;
import com.google.cloudcommerceprocurement.v1.model.Account;
import com.google.cloudcommerceprocurement.v1.model.ApproveAccountRequest;
import com.google.cloudcommerceprocurement.v1.model.ApproveEntitlementRequest;
import com.google.cloudcommerceprocurement.v1.model.ApproveEntitlementPlanChangeRequest;
import com.google.cloudcommerceprocurement.v1.model.Entitlement;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/** Client for performing actions against the Cloud Commerce Partner Procurement API. */
public class ProcurementService {

  private static final String CLOUD_SCOPE = "https://www.googleapis.com/auth/cloud-platform";

  private static final String PROVIDER_NAME =
      "providers/DEMO-" + ServiceOptions.getDefaultProjectId();
  private static final String ACCOUNT_NAME_PREFIX = PROVIDER_NAME + "/accounts/";
  private static final String ENTITLEMENT_NAME_PREFIX = PROVIDER_NAME + "/entitlements/";

  private CloudCommercePartnerProcurementService procurementService;

  public ProcurementService() throws GeneralSecurityException, IOException {
    procurementService =
        new CloudCommercePartnerProcurementService.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                new JacksonFactory(),
                new HttpCredentialsAdapter(
                    GoogleCredentials.getApplicationDefault().createScoped(
                        Collections.singletonList(CLOUD_SCOPE))))
            .setApplicationName(ServiceOptions.getDefaultProjectId()).build();
  }

  ////////////////////////////
  //   Account Operations   //
  ////////////////////////////

  /** Derives the Account ID from the resource name. */
  public static String getAccountId(String name) {
    return name.substring(ACCOUNT_NAME_PREFIX.length());
  }

  // Gets the name of an Account resource from its ID.
  private static String getAccountName(String id) {
    return ACCOUNT_NAME_PREFIX + id;
  }

  /** Gets an Account resource. */
  public Account getAccount(String id) throws IOException {
    String accountName = getAccountName(id);
    try {
      return procurementService.providers().accounts().get(accountName).execute();
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getCode() == 404) {
        return null;
      }
      throw e;
    }
  }

  /** Approves an Account resource. */
  public void approveAccount(String id) throws IOException {
    String accountName = getAccountName(id);
    ApproveAccountRequest request = new ApproveAccountRequest();
    request.setApprovalName("signup");
    procurementService.providers().accounts().approve(accountName, request).execute();
  }

  ////////////////////////////
  // Entitlement Operations //
  ////////////////////////////

  // Gets the name of an Account resource from its ID.
  private static String getEntitlementName(String id) {
    return ENTITLEMENT_NAME_PREFIX + id;
  }

  /** Gets an Entitlement resource. */
  public Entitlement getEntitlement(String id) throws IOException {
    String entitlementName = getEntitlementName(id);
    try {
      return procurementService.providers().entitlements().get(entitlementName).execute();
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getCode() == 404) {
        return null;
      }
      throw e;
    }
  }

  /** Approves an Entitlement resource. */
  public void approveEntitlement(String id) throws IOException {
    String entitlementName = getEntitlementName(id);
    ApproveEntitlementRequest request = new ApproveEntitlementRequest();
    procurementService.providers().entitlements().approve(entitlementName, request).execute();
  }

  /** Approves a plan change for an Entitlement resource. */
  public void approveEntitlementPlanChange(String id, String newPlan) throws IOException {
    String entitlementName = getEntitlementName(id);
    ApproveEntitlementPlanChangeRequest request = new ApproveEntitlementPlanChangeRequest();
    request.setPendingPlanName(newPlan);
    procurementService.providers().entitlements()
        .approvePlanChange(entitlementName, request)
        .execute();
  }
}
