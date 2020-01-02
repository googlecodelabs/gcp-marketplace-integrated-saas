## Quickstart

#### Setup
- Install [Maven](http://maven.apache.org/).
- Set the GOOGLE_APPLICATION_CREDENTIALS environment variable explicitly
  pointing to a downloaded service account credentials JSON file.
  
  Linux: export GOOGLE_APPLICATION_CREDENTIALS="[PATH]"
  Windows: set GOOGLE_APPLICATION_CREDENTIALS=[PATH]

#### Build
- Build your project with:
```
  mvn clean package -DskipTests
```

#### Ceate a subscription
```
  mvn exec:java -Dexec.mainClass=com.example.saas.Step1CreatePullSubscription
```

####  See the Cloud Pub/Sub messages
```
  mvn exec:java -Dexec.mainClass=com.example.saas.SubscriberMsg
```

#### Approving the account
```
  mvn exec:java -Dexec.mainClass=com.example.saas.Step2Account
```

#### Approve the entitlement
```
   mvn exec:java -Dexec.mainClass=com.example.saas.Step3EntitlementCreate
```

#### Approve changes to an entitlement
```
   mvn exec:java -Dexec.mainClass=com.example.saas.Step4EntitlementChange
```

#### Handle cancelled purchases
```
   mvn exec:java -Dexec.mainClass=com.example.saas.Step5EntitlementCancel
```
