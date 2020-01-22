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

package com.example.saas.step2account;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloudcommerceprocurement.v1.CloudCommercePartnerProcurementService;
import com.google.cloudcommerceprocurement.v1.model.Account;
import com.google.cloudcommerceprocurement.v1.model.ApproveAccountRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/** Client for performing actions against the Cloud Commerce Partner Procurement API. */
public class ProcurementService {

  private static final String CLOUD_SCOPE = "https://www.googleapis.com/auth/cloud-platform";

  private static final String PROVIDER_NAME =
      "providers/DEMO-" + ServiceOptions.getDefaultProjectId();
  private static final String ACCOUNT_NAME_PREFIX = PROVIDER_NAME + "/accounts/";

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
}
