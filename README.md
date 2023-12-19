
# Cloud Commerce Partner Procurement API Integration Codelab

A step by step guide to get familiar with the Google Cloud Marketplace procurement and service control APIs

The instructions for this codelab are located at
https://codelabs.developers.google.com/codelabs/gcp-marketplace-integrated-saas.

The onboarding guide is located at
https://cloud.google.com/marketplace/docs/partners/integrated-saas/.

## Languages

The codelab directions are specifically tailored to the [Python 2.7](python2.7/)
implementation.

There are also [Python 3](python3/) and [Java](java/) versions that
implement similar steps.

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

This codelab helps you to familiarize with the Google Cloud Marketplace procurement and service control APIs. Note this guide does not give a full product environment for testing. This codelab is intended for partners who have access to Producer Portal. If you do not have access to Producer Portal, and are using Partner Portal, follow the instructions in the Partner Portal version of this codelab.
