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

DATABASE_FILE = os.environ['PROCUREMENT_CODELAB_DATABASE']


class JsonDatabase(object):
    """JSON-based implementation of a simple file-based database."""

    def __init__(self):
        self.database = json.loads(open(DATABASE_FILE, 'r').read())

    def read(self, key):
        """Read the record with the given key from the database, if it exists."""
        if key in self.database:
            return self.database[key]
        return None

    def write(self, key, value):
        """Write the record with the given key to the database."""
        self.database[key] = value
        self.commit()

    def delete(self, key):
        """Delete the record with the given key from the database, if it exists."""
        if key in self.database:
            del self.database[key]
            self.commit()

    def commit(self):
        """Commits changes to the database by writing the in-memory dictionary."""
        with open(DATABASE_FILE, 'w') as f:
            json.dump(self.database, f)

    def items(self):
        """Provides a way to iterate over all elements in the database."""
        return self.database.items()
