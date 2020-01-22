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

/** Cloud Pub/Sub event types from Cloud Commerce Procurement. */
public enum ProcurementEventType {
  ACCOUNT_ACTIVE,
  ACCOUNT_DELETED,
  ENTITLEMENT_CREATION_REQUESTED,
  ENTITLEMENT_ACTIVE,
  ENTITLEMENT_PLAN_CHANGE_REQUESTED,
  ENTITLEMENT_PLAN_CHANGED,
  ENTITLEMENT_PLAN_CHANGE_CANCELLED,
  ENTITLEMENT_PENDING_CANCELLATION,
  ENTITLEMENT_CANCELLATION_REVERTED,
  ENTITLEMENT_CANCELLED,
  ENTITLEMENT_DELETED,
}
