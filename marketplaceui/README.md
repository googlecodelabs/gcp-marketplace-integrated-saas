# Integrate single sign-on (SSO) for your customers

This sample demonstrates how to verify GCP JWT token. The sample can be hosted at GCP App Engine, for more info see [here](https://cloud.google.com/appengine/docs/java/).

## Setup

### Initialize gcloud

    gcloud init

### Configure the working project

    gcloud config set project myProject

[Doc](https://cloud.google.com/sdk/gcloud/reference/config/set)

## Running locally

    mvn appengine:run

## Deploying

    mvn appengine:deploy
