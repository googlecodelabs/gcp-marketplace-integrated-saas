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

PROJECT_ID = os.environ['GOOGLE_CLOUD_PROJECT']

PROJECT_IAM_PAGE = 'https://console.cloud.google.com/iam-admin/iam?project={}'
PROJECT_PUBSUB_PAGE = 'https://console.cloud.google.com/apis/library/pubsub.googleapis.com?project={}'

TOPIC_PROJECT = 'cloudcommerceproc-prod'
TOPIC_NAME_PREFIX = 'DEMO-'
SUBSCRIPTION_NAME = 'codelab'


def main(argv):
    """Main entrypoint to the Pub/Sub subscription creation tool."""

    if len(argv) != 1:
        print 'Usage: python create_subscription.py'
        return

    subscriber = pubsub_v1.SubscriberClient()
    topic_path = subscriber.topic_path(TOPIC_PROJECT,
                                       TOPIC_NAME_PREFIX + PROJECT_ID)
    subscription_path = subscriber.subscription_path(PROJECT_ID,
                                                     SUBSCRIPTION_NAME)

    try:
        subscription = subscriber.create_subscription(subscription_path,
                                                      topic_path)
    except PermissionDenied:
        error_message = ('PERMISSION DENIED: Check that the Pub/Sub API is '
                         'enabled in your project and that your service '
                         'account was granted the Pub/Sub Editor role. \n'
                         'Check API status at: %s \n'
                         'Check IAM roles at: %s ' % (
                             PROJECT_PUBSUB_PAGE.format(PROJECT_ID),
                             PROJECT_IAM_PAGE.format(PROJECT_ID)))
        print error_message
        return

    print 'Subscription created: {}'.format(subscription)


if __name__ == '__main__':
    main(sys.argv)
