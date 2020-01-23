
# Cloud Commerce Partner Procurement API Integration Codelab

This is a step-by-step tutorial for integrating with the Cloud Commerce Partner
Procurement API to manage customer Accounts and Entitlements.

The instructions for this codelab are located at
https://codelabs.developers.google.com/codelabs/gcp-marketplace-integrated-saas.

The onboarding guide is located at
https://cloud.google.com/marketplace/docs/partners/integrated-saas/.

## Languages

The codelab directions are specifically tailored to the [Python 2.7](python2.7/)
implementation.

A [Java](java/) implementation also exists which has similar steps.

## Setup

The codelab relies on the values of several environment variables:
- **GOOGLE_APPLICATION_CREDENTIALS**
The path to your downloaded service account credentials JSON file.

- **GOOGLE_CLOUD_PROJECT**
The ID of your Google Cloud Project you're using for this codelab.

- **PROCUREMENT_CODELAB_DATABASE**
The path to the JSON database file you'll use for this codelab. Initially
populate this file with `{}`.

## Disclaimer

This is not an officially supported Google product.
