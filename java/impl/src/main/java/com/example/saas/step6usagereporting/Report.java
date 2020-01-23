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

package com.example.saas.step6usagereporting;

import com.example.saas.database.Customer;
import com.example.saas.database.Customer.Product;
import com.example.saas.database.JsonDatabase;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.servicecontrol.v1.ServiceControl;
import com.google.api.services.servicecontrol.v1.model.CheckRequest;
import com.google.api.services.servicecontrol.v1.model.CheckResponse;
import com.google.api.services.servicecontrol.v1.model.MetricValue;
import com.google.api.services.servicecontrol.v1.model.MetricValueSet;
import com.google.api.services.servicecontrol.v1.model.Operation;
import com.google.api.services.servicecontrol.v1.model.ReportRequest;
import com.google.cloud.ServiceOptions;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

/** Reports usage for all customers in the database. */
public class Report {

  private static final String CLOUD_SCOPE = "https://www.googleapis.com/auth/cloud-platform";

  /** Reports usage to Service Control. */
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Usage: Missing service_name argument.");
      return;
    }

    String serviceName = args[0];

    ServiceControl serviceControl =
        new ServiceControl.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                new JacksonFactory(),
                new HttpCredentialsAdapter(
                    GoogleCredentials.getApplicationDefault().createScoped(
                        Collections.singletonList(CLOUD_SCOPE))))
            .setApplicationName(ServiceOptions.getDefaultProjectId())
            .build();

    JsonDatabase db = new JsonDatabase();

    for (Customer customer : db.getAll()) {
      for (Product product : customer.products.values()) {
        if (product.consumerId == null || product.consumerId.isEmpty()) {
          continue;
        }

        String startTime =
            product.lastReportTime != null && !product.lastReportTime.isEmpty()
                ? product.lastReportTime
                : product.startTime;
        String endTime = Instant.now().toString();
        String metricPlanName = product.planId.replace('-', '_');

        Operation operation = new Operation();
        operation.setOperationId(UUID.randomUUID().toString());
        operation.setConsumerId(product.consumerId);
        operation.setOperationName("Codelab Usage Report");
        operation.setStartTime(startTime);
        operation.setEndTime(endTime);

        MetricValueSet metricValueSet = new MetricValueSet();
        metricValueSet.setMetricName(
            String.format("%s/%s_requests", serviceName, metricPlanName));

        MetricValue metricValue = new MetricValue();
        metricValue.setInt64Value(getUsageForProduct());

        metricValueSet.setMetricValues(Collections.singletonList(metricValue));
        operation.setMetricValueSets(Collections.singletonList(metricValueSet));

        CheckRequest checkRequest = new CheckRequest();
        checkRequest.setOperation(operation);
        CheckResponse response =
            serviceControl.services().check(serviceName, checkRequest).execute();

        if (response.getCheckErrors() != null && response.getCheckErrors().size() > 0) {
          System.out.printf(
              "Errors for user %s with product %s:\n",
              customer.procurementAccountId,
              product.productId);
          System.out.println(response.getCheckErrors());

          // TODO: Temporarily turn off service for the user.
          continue;
        }

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setOperations(Collections.singletonList(operation));
        serviceControl.services().report(serviceName, reportRequest).execute();

        product.lastReportTime = endTime;
        db.write(customer.procurementAccountId, customer);
      }
    }
  }

  // Gets usage for a product for a customer.
  private static long getUsageForProduct() {
    // TODO: Get the usage since the last report time.
    return 10;
  }
}
