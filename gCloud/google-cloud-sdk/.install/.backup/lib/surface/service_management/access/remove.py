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

"""Command to remove a principal from a service's access policy."""

from googlecloudsdk.api_lib.service_management import base_classes
from googlecloudsdk.api_lib.service_management import services_util
from googlecloudsdk.calliope import base
from googlecloudsdk.calliope import exceptions
from googlecloudsdk.third_party.apitools.base.py import exceptions as apitools_exceptions


class Remove(base.Command, base_classes.AccessCommand):
  """Removes a principal from a service's access policy."""

  @staticmethod
  def Args(parser):
    """Args is called by calliope to gather arguments for this command.

    Args:
      parser: An argparse parser that you can use to add arguments that go
          on the command line after this command. Positional arguments are
          allowed.
    """

    parser.add_argument(
        '--service',
        help='The service from which the principal is to be removed.',
        required=True)
    parser.add_argument(
        '--label',
        help=('Optionally, the visibility label from which the principal is '
              'to be removed.'))
    parser.add_argument(
        'type',
        help=('The type of principal to remove from the access policy entity. '
              'Choose from {0}.').format(
                  ', '.join(sorted(Remove._PRINCIPAL_TYPES))),
        type=lambda x: str(x).lower(),
        choices=sorted(Remove._PRINCIPAL_TYPES))
    parser.add_argument(
        'principal',
        help='The user or group email to remove from the access policy entity.')

  def Run(self, args):
    """Run 'service-management access remove'.

    Args:
      args: argparse.Namespace, The arguments that this command was invoked
          with.

    Returns:
      The response from the access API call.

    Raises:
      HttpException: An http error response was received while executing api
        request.
      ToolException: An error such as specifying a label that doesn't exist
        or a principal that is already a member of the service or visibility
        label.
    """
    request = (self.services_messages
               .ServicemanagementServicesGetAccessPolicyRequest(
                   serviceName=args.service))

    if not services_util.ValidateEmailString(args.principal):
      raise exceptions.ToolException('Invalid email string')

    try:
      access_policy = self.services_client.services.GetAccessPolicy(request)
    except apitools_exceptions.HttpError as error:
      raise exceptions.HttpException(services_util.GetError(error))

    # Fill in the serviceName field if GetAccessPolicy didn't do so for us.
    if not access_policy.serviceName:
      access_policy.serviceName = args.service

    # Fill in the accessList field if GetAccessPolicy didn't do so for us.
    if not access_policy.accessList:
      access_policy.accessList = self.services_messages.ServiceAccessList()

    principal_string = self._BuildPrincipalString(args.principal, args.type)

    if args.label:
      # If a visibility label is provided, try to remove the principal from it.
      self._RemovePrincipalFromLabel(principal_string, args.service,
                                     args.label, access_policy)
    else:
      # Otherwise, try to remove principal from the service access policy
      # directly.
      self._RemovePrincipalFromService(principal_string, args.service,
                                       access_policy)

    # Send updated access policy to backend
    try:
      return self.services_client.services.UpdateAccessPolicy(access_policy)
    except apitools_exceptions.HttpError as error:
      raise exceptions.HttpException(services_util.GetError(error))

  def _RemovePrincipalFromLabel(self, principal, service, label,
                                access_policy):
    """Removes a principal from a service's access policy under a label.

    Args:
      principal: The principal to remove from the service access policy.
          Note that this string must already begin with "user:" or "group:".
      service: The name of the service to modify
      label: The name of the visibility label from which to remove the principal
      access_policy: The access policy to modify. It will be edited in-place.

    Raises:
      exceptions.ToolException: The principal was not a member of the
          visibility label or the visibility label doesn't exist.
    """
    # First, check to see if the project has the label. If so, remove the
    # principal from that label, if present.
    lists = (self.services_messages
             .ServiceAccessPolicy.VisibilityLabelAccessListsValue())
    if access_policy and access_policy.visibilityLabelAccessLists:
      lists = access_policy.visibilityLabelAccessLists

    for access_list in lists.additionalProperties:
      if access_list.key == label:
        found_list_members = access_list.value.members
        if principal not in found_list_members:
          raise exceptions.ToolException(
              '%s is not a member of visibility label %s for service %s.'
              % (principal, label, service))
        else:
          # Remove the targeted principal from the label's access list,
          # then return.
          found_list_members.remove(principal)
          return

    # Raise an exception if the label doesn't exist for the service
    raise exceptions.ToolException(
        'No visibility label named %s for service %s.'
        % (label, service))

  def _RemovePrincipalFromService(self, principal, service, access_policy):
    """Removes a principal from a service's access policy.

    Args:
      principal: The principal to remove fromthe service access policy.
          Note that this string must already begin with "user:" or "group:".
      service: The name of the service to modify
      access_policy: The access policy to modify. It will be edited in-place.

    Raises:
      exceptions.ToolException: The principal was already a member of the
          service.
    """

    if principal not in access_policy.accessList.members:
      # If principal is not already a member of the service,
      # raise exception now.
      raise exceptions.ToolException(
          '%s is not a member of service %s.' % (principal, service))
    else:
      # Otherwise, remove the principal from the service access policy.
      access_policy.accessList.members.remove(principal)
