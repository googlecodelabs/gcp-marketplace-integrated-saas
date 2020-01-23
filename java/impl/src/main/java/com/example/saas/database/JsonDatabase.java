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

import com.example.saas.database.Customer.Product;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** A JSON-based implementation of a simple file-based database. */
public class JsonDatabase {

  private static final String DATABASE_FILE_ENVIRONMENT_VARIABLE = "PROCUREMENT_CODELAB_DATABASE";

  private Gson gson;
  private Map<String, Customer> customers;
  private String databasePath;

  public JsonDatabase() throws IOException {
    databasePath = System.getenv(DATABASE_FILE_ENVIRONMENT_VARIABLE);
    gson =
        new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    File file = new File(databasePath);
    String json = Files.toString(file, Charsets.UTF_8);

    Map<String, Customer> jsonCustomers =
        gson.fromJson(json, new TypeToken<Map<String, Customer>>() {}.getType());
    customers = new HashMap<>(jsonCustomers);
  }

  /** Reads a record from the database. */
  public Customer read(String id) {
    return customers.get(id);
  }

  /** Writes a record to the database. */
  public void write(String id, Customer customer) throws IOException {
    customers.put(id, customer);
    commit();
  }

  /** Deletes a record from the database. */
  public void delete(String id) throws IOException {
    customers.remove(id);
    commit();
  }

  // Commits the in-memory data to disk.
  private void commit() throws IOException {
    File file = new File(databasePath);
    FileWriter writer = new FileWriter(file);
    gson.toJson(customers, writer);
    writer.close();
  }

  /** Provides a way to iterate over all elements in the database. */
  public Collection<Customer> getAll() {
    return customers.values();
  }
}
