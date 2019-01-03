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
import uuid

from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
from google.cloud import pubsub_v1
from google.oauth2 import service_account

from impl.database.database import JsonDatabase

PUBSUB_SUBSCRIPTION = 'codelab'

CREDENTIALS_FILE = '../account.json'
PROCUREMENT_API = 'cloudcommerceprocurement'


def _generate_internal_account_id():
    ### TODO: Replace with whatever ID generation code already exists. ###
    return str(uuid.uuid4())


class Procurement(object):
    """Utilities for interacting with the Procurement API."""

    def __init__(self, credentials, database, project_id):
        self.service = build(PROCUREMENT_API, 'v1', credentials=credentials,
                             cache_discovery=False)
        self.project_id = project_id
        self.database = database

    ##########################
    ### Account operations ###
    ##########################

    def _get_account_name(self, account_id):
        return 'providers/DEMO-{}/accounts/{}'.format(self.project_id,
                                                      account_id)

    def get_account(self, account_id):
        """Gets an account from the Procurement Service."""
        name = self._get_account_name(account_id)
        request = self.service.providers().accounts().get(name=name)
        try:
            response = request.execute()
            return response
        except HttpError as err:
            if err.resp.status == 404:
                return None

    def approve_account(self, account_id):
        """Approves the account in the Procurement Service."""
        name = self._get_account_name(account_id)
        request = self.service.providers().accounts().approve(
            name=name, body={'approvalName': 'signup'})
        request.execute()

    def handle_account_message(self, message):
        """Handles incoming Pub/Sub messages about account resources."""

        account_id = message['id']

        customer = self.database.read(account_id)
        account = self.get_account(account_id)

        ############################## IMPORTANT ##############################
        ### In true integrations, Pub/Sub messages for new accounts should  ###
        ### be ignored. Account approvals are granted as a one-off action   ###
        ### during customer sign up. This codelab does not include the sign ###
        ### up flow, so it chooses to approve accounts here instead.        ###
        ### Production code for real, non-codelab services should never     ###
        ### blindly approve these. The following should be done as a result ###
        ### of a user signing up.                                           ###
        #######################################################################
        if account:
            approval = None
            for account_approval in account['approvals']:
                if account_approval['name'] == 'signup':
                    approval = account_approval
                    break

            if approval:
                if approval['state'] == 'PENDING':
                    # See above note. Actual production integrations should not
                    # approve blindly when receiving a message.
                    self.approve_account(account_id)

                elif approval['state'] == 'APPROVED':
                    # Now that it's approved, store a record in the database.
                    internal_id = _generate_internal_account_id()
                    customer = {
                        'procurement_account_id': account_id,
                        'internal_account_id': internal_id,
                        'products': {}
                    }
                    self.database.write(account_id, customer)
            else:
                # The account has been deleted, so delete the database record.
                if customer:
                    self.database.delete(account_id)

        # Always ack account messages. We only care about the above scenarios.
        return True

    ##############################
    ### Entitlement operations ###
    ##############################

    def handle_entitlement_message(self):
        ### TODO: Complete in section 3. ###
        return False


def _get_credentials():
    # The credentials use the JSON keyfile generated during service account
    # creation on the Cloud Console.
    return service_account.Credentials.from_service_account_file(
        os.path.join(os.path.dirname(__file__), CREDENTIALS_FILE),
        scopes=['https://www.googleapis.com/auth/cloud-platform'])


def main(argv):
    """Main entrypoint to the integration with the Procurement Service."""

    if len(argv) < 2:
        print 'Usage: python -m impl.step_2_account.app <project_id>'
        return

    project_id = argv[1]

    credentials = _get_credentials()

    # Construct a service for the Partner Procurement API.
    database = JsonDatabase()
    procurement = Procurement(credentials, database, project_id)

    # Get the subscription object in order to perform actions on it.
    subscriber = pubsub_v1.SubscriberClient(credentials=credentials)
    subscription_path = subscriber.subscription_path(project_id,
                                                     PUBSUB_SUBSCRIPTION)

    def callback(message):
        """Callback for handling Cloud Pub/Sub messages."""
        payload = json.loads(message.data)

        print 'Received message:'
        pprint.pprint(payload)
        print

        ack = False
        if 'entitlement' in payload:
            ack = procurement.handle_entitlement_message()
        elif 'account' in payload:
            ack = procurement.handle_account_message(payload['account'])
        else:
            # If there's no account or entitlement, then just ack and ignore the
            # message. This should never happen.
            ack = True

        if ack:
            message.ack()

    subscription = subscriber.subscribe(subscription_path, callback=callback)

    print 'Listening for messages on {}'.format(subscription_path)
    print 'Exit with Ctrl-\\'

    while True:
        try:
            subscription.result()
        except Exception as exception:
            print('Listening for messages on {} threw an Exception: {}.'.format(
                subscription_path, exception))


if __name__ == '__main__':
    main(sys.argv)
