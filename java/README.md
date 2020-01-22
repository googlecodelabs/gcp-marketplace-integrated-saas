# Java Codelab

The Java version of the codelab requires the user to generate an API client for
the Cloud Commerce Partner Procurement API. The codelab source code will not
compile and run without the client being generated and added as a build
dependency.

## Setup

1. Install [Maven](http://maven.apache.org/).

1. Download a local copy of the Partner Procurement API discovery document:

        wget 'https://cloudcommerceprocurement.googleapis.com/$discovery/rest?version=v1' -O discovery.json

1. Follow the instructions at https://github.com/google/apis-client-generator to
   install the Google API client generator.
   
   Note: The `generate_library` binary many not be put in the correct location,
   so make note of where it was placed in the output of the installation.

1. Generate an API client using the `generate_library` tool.

       generate_library \
         --input=discovery.json
         --language=java
         --output_dir=procurement
   
   The above generates a client library in a newly created `procurement/`
   subdirectory.

1. Install the client library by running `mvn clean install` from inside of the
   `procurement/` directory.

1. Add a new dependency to the `pom.xml` file inside of the `impl/` directory

       <dependency>
         <groupId>com.google.apis</groupId>
         <artifactId>google-api-services-cloudcommerceprocurement</artifactId>
         <version>ADD CORRECT VERSION</version>
         <scope>compile</scope>
       </dependency>

1. Build the codelab implementation with `mvn package` and verify it compiles.
