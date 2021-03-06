# Copyright 2014 Google Inc. All Rights Reserved.
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
"""Command for describing instances."""
from googlecloudsdk.api_lib.compute import base_classes
from googlecloudsdk.api_lib.compute import resource_specs


class Describe(base_classes.ZonalDescriber):
  """Describe a virtual machine instance."""

  @staticmethod
  def Args(parser):
    base_classes.ZonalDescriber.Args(parser, 'compute.instances')
    base_classes.AddFieldsFlag(parser, 'instances')

  @property
  def service(self):
    return self.compute.instances

  @property
  def resource_type(self):
    return 'instances'

  def ComputeDynamicProperties(self, args, items):
    # This is an overridden function that modifies the output of the custom
    # machine type in instances describe.
    items_dict = items.next()
    machine_type = resource_specs.FormatDescribeMachineTypeName(
        items_dict,
        args.command_path)
    if machine_type:
      items_dict['machineType'] = machine_type
    yield items_dict

Describe.detailed_help = {
    'brief': 'Describe a virtual machine instance',
    'DESCRIPTION': """\
        *{command}* displays all data associated with a Google Compute
        Engine virtual machine instance.
        """,
}
