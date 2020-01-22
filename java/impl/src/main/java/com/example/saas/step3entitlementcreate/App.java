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

package com.example.saas.step3entitlementcreate;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;

public class App {
  private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
  private static final String SUBSCRIPTION_ID = "codelab";

  public static void main(String[] args) throws Exception {
    ProjectSubscriptionName subscriptionName =
        ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID);
    Subscriber subscriber =
        Subscriber.newBuilder(subscriptionName, new ProcurementMessageReceiver()).build();
    subscriber.startAsync().awaitRunning();
    subscriber.awaitTerminated();
  }
}
