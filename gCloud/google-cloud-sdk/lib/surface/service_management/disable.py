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

"""service-management disable command."""

from googlecloudsdk.api_lib.service_management import base_classes
from googlecloudsdk.api_lib.service_management import base_classes
from googlecloudsdk.api_lib.service_management import consumers_flags
from googlecloudsdk.api_lib.service_management import services_util
from googlecloudsdk.calliope import base
from googlecloudsdk.calliope import exceptions
from googlecloudsdk.third_party.apitools.base.py import exceptions as apitools_exceptions


class Disable(base.Command, base_classes.BaseServiceManagementCommand):
  """Disables a service for a consumer project."""

  @staticmethod
  def Args(parser):
    """Args is called by calliope to gather arguments for this command.

    Args:
      parser: An argparse parser that you can use to add arguments that go
          on the command line after this command. Positional arguments are
          allowed.
    """
    consumers_flags.CONSUMER_PROJECT_FLAG.AddToParser(parser)
    consumers_flags.SERVICE_FLAG.AddToParser(parser)

  def Run(self, args):
    """Run 'service-management disable'.

    Args:
      args: argparse.Namespace, The arguments that this command was invoked
          with.

    Returns:
      The response from the consumer settings API call.

    Raises:
      HttpException: An http error response was received while executing api
          request.
    """
    # Shorten the patch request name for better readability
    patch_request = (self.services_messages
                     .ServicemanagementServicesProjectSettingsPatchRequest)

    usage_settings = self.services_messages.UsageSettings(
        consumerEnableStatus=(self.services_messages.UsageSettings
                              .ConsumerEnableStatusValueValuesEnum.DISABLED))

    project_settings = self.services_messages.ProjectSettings(
        usageSettings=usage_settings)

    request = patch_request(
        serviceName=args.service,
        consumerProjectId=args.consumer_project,
        projectSettings=project_settings,
        updateMask='usage_settings.consumer_enable_status')

    try:
      # TODO(user): Add support for Operation completion, and --async flag
      result = self.services_client.services_projectSettings.Patch(request)
    except apitools_exceptions.HttpError as error:
      raise exceptions.HttpException(services_util.GetError(error))

    return services_util.ProcessOperationResult(result)
