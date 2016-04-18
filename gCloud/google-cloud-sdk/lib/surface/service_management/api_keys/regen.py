# Copyright 2016 Google Inc. All Rights Reserved.
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

"""Implementation of the service-management api-keys regen command."""

from googlecloudsdk.api_lib.service_management import base_classes
from googlecloudsdk.api_lib.service_management import services_util

from googlecloudsdk.calliope import base
from googlecloudsdk.calliope import exceptions
from googlecloudsdk.third_party.apitools.base.py import exceptions as apitools_exceptions


class Regen(base.Command, base_classes.BaseServiceManagementCommand):
  """Regenerates the keystring for the specified API key.

     After regenerating the keystring for the API key, the old keystring is
     stored in the 'previous_key' field. You can switch back to using the
     previous_key by calling the revert command.
  """

  @staticmethod
  def Args(parser):
    """Args is called by calliope to gather arguments for this command.

    Args:
      parser: An argparse parser that you can use to add arguments that go
          on the command line after this command. Positional arguments are
          allowed.
    """
    parser.add_argument('--key',
                        '-k',
                        help='The identifier of the API key to be regenerated.')

  def Run(self, args):
    """Run 'service-management api-keys regen'.

    Args:
      args: argparse.Namespace, The arguments that this command was invoked
          with.

    Returns:
      The response from the keys API call.

    Raises:
      HttpException: An http error response was received while executing api
          request.
    """
    # Construct the Regenerate API Key request object
    request = self.apikeys_messages.ApikeysProjectsApiKeysRegenerateRequest(
        projectId=self.project,
        keyId=args.key)

    try:
      return self.apikeys_client.projects_apiKeys.Regenerate(request)
    except apitools_exceptions.HttpError as error:
      raise exceptions.HttpException(services_util.GetError(error))
