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

import os
import sys

from googleapiclient.discovery import build
from google.oauth2 import service_account

CREDENTIALS_FILE = '../account.json'
PROCUREMENT_API = 'cloudcommerceprocurement'


def _get_account_name(project_id, account_id):
    return 'providers/DEMO-{}/accounts/{}'.format(project_id, account_id)


def _get_credentials():
    """Generates credentials for issuing requests."""

    # The credentials use the JSON keyfile generated during service account
    # creation on the Cloud Console.
    return service_account.Credentials.from_service_account_file(
        os.path.join(os.path.dirname(__file__), CREDENTIALS_FILE),
        scopes=['https://www.googleapis.com/auth/cloud-platform'])


def main(argv):
    """Main entrypoint to the Account reset tool."""

    if len(argv) < 3:
        print 'Usage: python reset_account.py <project_id> <account_id>'
        return

    project_id = argv[1]
    account_id = argv[2]

    credentials = _get_credentials()
    procurement = build(PROCUREMENT_API, 'v1', credentials=credentials,
                        cache_discovery=False)

    account_name = _get_account_name(project_id, account_id)
    request = procurement.providers().accounts().reset(name=account_name)
    request.execute()


if __name__ == '__main__':
    main(sys.argv)
