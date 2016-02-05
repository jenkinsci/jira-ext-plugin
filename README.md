# jira-ext-plugin
* [Wiki][wiki]
* [Issue Tracking][issues]

A plugin for Jenkins CI to update JIRA tickets in an extensible way: both what to update and how to update are exposed as `ExtensionPoint`s.

Out of the box, you may discover issues by:

* The issue key as the first word of the commit message
* The issue key as the first word of the upstream commit message
* Manually specifying an issue
* Looking for it being mentioned somewhere in the commit message

To all of these JIRA issues, you may:

* Add a comment
* Update a field
* Add a label
* Transition a ticket (`Start Work`, `Resolve`, etc..)

Example UI:

![jira-ext-1](https://cloud.githubusercontent.com/assets/731899/12099841/5d1671e8-b2e8-11e5-9c6d-d28073ad7c15.png)

# Comparison to jira-plugin

The jira-ext plugin is meant to be extensible in ways that the jira-plugin is not. Both Jenkins and JIRA are used as part of an organizations workflow, and as such are highly customizable. The jira-ext plugin continues this philosophy by making the actions (and when to perform those actions) also customizable for your organization.

In terms of specific features:

jira-ext plugin:

1. Specify issues manually
2. Specify issues from upstream commits
3. Transitions issues
4. Add label to issues
5. Update custom fields
6. Customize comments on issues

jira-plugin:

1. Perform release notes
2. (Deprecated) support for SOAP
3. Specify issues from JQL
4. Automatically link back to jenkins builds (backpointers)
5. Link JIRA issues in the Jenkins UI


# Extending the plugin

The jira-ext plugin is built with extension in mind. Both how to discover issues and what action to take are customizable. They are implemented as ExtensionPoint, meaning your plugin (or a custom plugin) can contribute its own ticket discovery strategies or operations based on logic specific to your installation or environment. 

For example, a code review build could provide a link to the code review server in the JIRA issue as a custom field, or as a comment.

Or another example, a production release build could update tickets based on differences between tags, to keep track of what was deployed last.

If you want to add an operation which should be part of any JIRA install, please open a pull request.

## Add a custom `Discover Issues By` option

You may update what tickets to update using an `IssueStrategyExtension` and `IssueStrategyExtensionDescriptor`. 
See `SingleTicketStrategy` for an example.

## Add a custom JIRA operation
You may add your own operations using a `JiraOperationExtension` and `JiraOperationExtensionDescriptor`. See `AddComment` for an example.

# Authors
Dan Alvizu <alvizu@gmail.com>

#License
Licensed under the Apache License, Version 2.0 (the “License”); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
“AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
 
[issues]: https://issues.jenkins-ci.org/issues/?jql=project%20%3D%20JENKINS%20AND%20status%20in%20(Open%2C%20%22In%20Progress%22%2C%20Reopened)%20AND%20component%20%3D%20%27jira-ext-plugin%27
[wiki]: https://wiki.jenkins-ci.org/display/JENKINS/Jira-Ext+Plugin
