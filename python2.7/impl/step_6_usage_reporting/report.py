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

import datetime
import os
import sys
import uuid

from googleapiclient.discovery import build, build_from_document

from impl.database.database import JsonDatabase

STAGING_DISCOVERY_FILE = 'staging_servicecontrol_discovery.json'
TIME_FORMAT = '%Y-%m-%dT%H:%M:%SZ'


def _get_usage_for_product():
    ### TODO: Get the usage since the last report time. ###
    return '10'


def _get_staging_discovery():
    with open(
        os.path.join(os.path.dirname(__file__), STAGING_DISCOVERY_FILE),
        'r') as f:
        return f.read()


def main(argv):
    """Sends usage reports to Google Service Control for all users."""

    if len(argv) != 2:
        print('Usage: python -m impl.step_6_report_usage.report <service_name>')
        return

    service_name = argv[1]

    service = build('servicecontrol', 'v1')

    database = JsonDatabase()

    for customer_id, customer in database.iteritems():
        for product_id, product in customer['products'].iteritems():
            if 'consumer_id' not in product:
                continue
            end_time = datetime.datetime.utcnow().strftime(TIME_FORMAT)
            start_time = None
            if 'last_report_time' in product:
                start_time = product['last_report_time']
            else:
                start_time = product['start_time']
            metric_plan_name = product['plan_id'].replace('-', '_')
            operation = {
                'operationId': str(uuid.uuid4()),
                'operationName': 'Codelab Usage Report',
                'consumerId': product['consumer_id'],
                'startTime': start_time,
                'endTime': end_time,
                'metricValueSets': [{
                    'metricName': '%s/%s_requests' % (service_name,
                                                      metric_plan_name),
                    'metricValues': [{
                        'int64Value': _get_usage_for_product(),
                    }],
                }],
            }
            check = service.services().check(
                serviceName=service_name, body={
                    'operation': operation
                }).execute()

            if 'checkErrors' in check:
                print 'Errors for user %s with product %s:' % (customer_id,
                                                               product_id)
                print check['checkErrors']
                ### TODO: Temporarily turn off service for the user. ###
                continue
            service.services().report(
                serviceName=service_name, body={
                    'operations': [operation]
                }).execute()
            product['last_report_time'] = end_time
            database.write(customer_id, customer)


if __name__ == '__main__':
    main(sys.argv)
