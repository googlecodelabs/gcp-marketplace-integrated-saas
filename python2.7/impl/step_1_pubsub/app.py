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

import json
import os
import pprint
import sys

from google.cloud import pubsub_v1

PROJECT_ID = os.environ['GOOGLE_CLOUD_PROJECT']

PUBSUB_SUBSCRIPTION = 'codelab'


def main(argv):
    """Main entrypoint to the integration with the Procurement Service."""

    if len(argv) != 1:
        print('Usage: python -m impl.step_1_pubsub.app')
        return

    # Get the subscription object in order to perform actions on it.
    subscriber = pubsub_v1.SubscriberClient()
    subscription_path = subscriber.subscription_path(PROJECT_ID,
                                                     PUBSUB_SUBSCRIPTION)

    def callback(message):
        """Callback for handling Cloud Pub/Sub messages."""
        payload = json.loads(message.data)

        print('Received message:')
        pprint.pprint(payload)
        print()

    subscription = subscriber.subscribe(subscription_path, callback=callback)

    print('Listening for messages on {}'.format(subscription_path))
    print('Exit with Ctrl-\\')

    while True:
        try:
            subscription.result()
        except Exception as exception:
            print('Listening for messages on {} threw an Exception: {}.'.format(
                subscription_path, exception))


if __name__ == '__main__':
    main(sys.argv)
