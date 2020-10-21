## Quickstart

#### Setup
- Install [Maven](http://maven.apache.org/).

#### Build
- Build your project with:
```
  mvn clean package -DskipTests
```

#### Listen to Cloud Pub/Sub messages
```
  mvn exec:java -Dexec.mainClass=com.example.saas.step1pubsub.App
```
#### Approve customer Accounts
```
  mvn exec:java -Dexec.mainClass=com.example.saas.step2account.App
```

#### Approve customer Entitlements
```
  mvn exec:java -Dexec.mainClass=com.example.saas.step3entitlementcreate.App
```

#### Manage customer Entitlement changes
```
   mvn exec:java -Dexec.mainClass=com.example.saas.step4entitlementchange.App
```

#### Manage customer Entitlement cancellations
```
   mvn exec:java -Dexec.mainClass=com.example.saas.step5entitlementcancel.App
```

#### Report customer usage of the service
```
   mvn exec:java -Dexec.mainClass=com.example.saas.step6usagereporting.Report -Dexec..args=isaas-codelab.mp-marketplace-partner-demos.appspot.com
```
