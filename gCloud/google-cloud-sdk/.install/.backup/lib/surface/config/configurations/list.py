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

"""Command to list named configuration."""

from googlecloudsdk.calliope import base
from googlecloudsdk.core import named_configs
from googlecloudsdk.core import properties


class List(base.Command):
  """Lists existing named configurations."""

  detailed_help = {
      'DESCRIPTION': """\
          {description}

          Run `$ gcloud topic configurations` for an overview of named
          configurations.
          """,
      'EXAMPLES': """\
          To list all available configurations, run:

            $ {command}
          """,
  }

  def Run(self, args):
    configs = named_configs.ListNamedConfigs(log_warnings=True)
    for config in configs:
      fname = named_configs.GetPathForConfigName(config.name)
      config_props = properties.VALUES.AllValues(
          list_unset=True,
          properties_file=properties.PropertiesFile([fname]),
          only_file_contents=True)
      yield {
          'name': config.name,
          'is_active': config.is_active,
          'properties': config_props,
      }

  def Format(self, args):
    return ('table('
            'name,'
            'is_active,'
            'properties.core.account,'
            'properties.core.project,'
            'properties.compute.zone:label=DEFAULT_ZONE,'
            'properties.compute.region:label=DEFAULT_REGION)')
