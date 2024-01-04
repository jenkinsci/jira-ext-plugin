# Old Changelog

# Future entries are located in [GitHub releases](https://github.com/jenkinsci/jira-ext-plugin/releases/).

## 0.8 March 23 2019

- Modify MentionedInCommitStrategy to use the comment of the change // [pull\#10](https://github.com/jenkinsci/jira-ext-plugin/pull/10)
- Add option to add label to a custom field // [pull \#17](https://github.com/jenkinsci/jira-ext-plugin/pull/17)
- Fix issue with non-standard JIRA issue keys // [pull \#16](https://github.com/jenkinsci/jira-ext-plugin/pull/16)
- Fix regex search for JIRA issue key in first word of commit // [pull \#15](https://github.com/jenkinsci/jira-ext-plugin/pull/15)
- Add Jenkinsfile // [pull \#11](https://github.com/jenkinsci/jira-ext-plugin/pull/11)
- Fix MentionedInCommitStrategy for tickets in multiple lines // [pull \#9](https://github.com/jenkinsci/jira-ext-plugin/pull/9)

## 0.7 June 15 2017

- Add support for conditional step plugin [JENKINS-35339](https://issues.jenkins-ci.org/browse/JENKINS-35339)

## [0.6.1](https://github.com/jenkinsci/jira-ext-plugin/releases/tag/jira-ext-0.6.1) December 15 2016

- Add a Publisher for jira-ext steps, so that they can be run as a post-build step

## [0.5](https://github.com/jenkinsci/jira-ext-plugin/releases/tag/jira-ext-0.5) May 24 2016

- Add environment variable support for update field, add label, add fix version, single ticket strategy [JENKINS-33054](https://issues.jenkins-ci.org/browse/JENKINS-33054)

## [0.4.1](https://github.com/jenkinsci/jira-ext-plugin/releases/tag/jira-ext-0.4.1) March 4 2016

- Fix issue with guice injection affecting job-dsl runs

## [0.4](https://github.com/jenkinsci/jira-ext-plugin/releases/tag/jira-ext-0.4) March 3 2016

- Add methods to JiraSvc to get field values, update multi-select values, and directly obtain a JiraClient
- Add UI control to discover fieldIds in 'Update a Field' JIRA operation
- Add socket and connection timeouts (10s default, configured in global config)
- Add ability set the Fix-Version of an issue
- Fix bug with global config not persisting between restarts [JENKINS-33249](https://issues.jenkins-ci.org/browse/JENKINS-33249)
- Fix docs for fieldIds [JENKINS-33055](https://issues.jenkins-ci.org/browse/JENKINS-33055)

## [0.3](https://github.com/jenkinsci/jira-ext-plugin/releases/tag/jira-ext-0.3)

- Failed release - eaten by [INFRA-588](https://issues.jenkins-ci.org/browse/INFRA-588)

## [0.2](https://github.com/jenkinsci/jira-ext-plugin/releases/tag/jira-ext-0.2) Feb 4 2016

- Add Job-DSL support
- Add 'Mentioned in commit message' Issue Strategy

## [0.1](https://github.com/jenkinsci/jira-ext-plugin/releases/tag/jira-ext-0.1) Jan 11 2016

- Initial release
