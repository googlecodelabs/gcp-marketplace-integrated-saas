# Copyright 2018 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import sys

from google.api_core.exceptions import PermissionDenied
from google.cloud import pubsub_v1
from google.oauth2 import service_account

PROJECT_IAM_PAGE = 'https://console.cloud.google.com/iam-admin/iam?project={}'

TOPIC_PROJECT = 'cloudcommerceproc-prod'
TOPIC_NAME_PREFIX = 'DEMO-'
SUBSCRIPTION_NAME = 'codelab'

CREDENTIALS_FILE = '../account.json'


def _get_credentials():
    """Generates credentials for issuing requests."""

    # The credentials use the JSON keyfile generated during service account
    # creation on the Cloud Console.
    return service_account.Credentials.from_service_account_file(
        CREDENTIALS_FILE,
        scopes=['https://www.googleapis.com/auth/cloud-platform'])


def main(argv):
    """Main entrypoint to the Pub/Sub subscription creation tool."""

    if len(argv) < 2:
        print 'Usage: python create_subscription.py <project_id>'
        return

    project_id = argv[1]

    credentials = _get_credentials()

    subscriber = pubsub_v1.SubscriberClient(credentials=credentials)
    topic_path = subscriber.topic_path(TOPIC_PROJECT,
                                       TOPIC_NAME_PREFIX + project_id)
    subscription_path = subscriber.subscription_path(project_id,
                                                     SUBSCRIPTION_NAME)

    try:
        subscription = subscriber.create_subscription(subscription_path,
                                                      topic_path)
    except PermissionDenied:
        print 'PERMISSION DENIED: Check that your service account was granted '
        print 'the Pub/Sub Editor role. Go to: %s' % PROJECT_IAM_PAGE.format(
            project_id)
        return

    print 'Subscription created: {}'.format(subscription)


if __name__ == '__main__':
    main(sys.argv)
