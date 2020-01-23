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

package com.example.saas.database;

import java.util.Map;

/** POJO for information about a customer of a provider. */
public class Customer {
  public String procurementAccountId;
  public String internalAccountId;
  public Map<String, Product> products;

  /** POJO for information about a customer's purchase of a product. */
  public static class Product {
    public String productId;
    public String planId;
    public String consumerId;
    public String startTime;
    public String lastReportTime;
  }
}
