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
import time

from googleapiclient.discovery import build

PROJECT_ID = os.environ['GOOGLE_CLOUD_PROJECT']

PROCUREMENT_API = 'cloudcommerceprocurement'


def _get_account_name(account_id):
    return 'providers/DEMO-{}/accounts/{}'.format(PROJECT_ID, account_id)


def main(argv):
    """Main entrypoint to the Account reset tool."""

    if len(argv) != 2:
        print('Usage: python3 reset_account.py <account_id>')
        return

    account_id = argv[1]

    procurement = build(PROCUREMENT_API, 'v1', cache_discovery=False)

    account_name = _get_account_name(account_id)
    request = procurement.providers().accounts().reset(name=account_name)
    request.execute()

    # The account is reset as pending approval status. There is no
    # account message notifying API clients. To unblock the testing
    # approve the account.
    time.sleep(20) # Waiting for reset completion
    approve_request = procurement.providers().accounts().approve(
        name=account_name, body={'approvalName': 'signup'})
    approve_request.execute()

if __name__ == '__main__':
    main(sys.argv)
