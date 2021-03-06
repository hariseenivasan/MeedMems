# Copyright 2015 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Command for deleting user-managed service account keys."""

from googlecloudsdk.api_lib.iam import base_classes
from googlecloudsdk.api_lib.iam import utils
from googlecloudsdk.core import log


class Delete(base_classes.BaseIamCommand):
  """Delete a user-managed key from a service account."""

  @staticmethod
  def Args(parser):
    parser.add_argument('--iam-account',
                        required=True,
                        help='The service account whose key to '
                        'delete.')

    parser.add_argument('key',
                        metavar='KEY-ID',
                        help='The key to delete.')

  @utils.CatchServiceAccountErrors
  def Run(self, args):
    self.SetAddressAndKey(args.iam_account, args.key)
    self.iam_client.projects_serviceAccounts_keys.Delete(
        self.messages.IamProjectsServiceAccountsKeysDeleteRequest(
            name=utils.EmailAndKeyToResourceName(args.iam_account, args.key)))

    log.status.Print(
        'deleted key [{1}] for service account [{0}]'.format(
            args.iam_account,
            args.key))
