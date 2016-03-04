# Using the Job-DSL plugin

The jira-ext plugin can be configured via job-dsl plugin:

```
job {
  //...
  steps {
    //...
    updateJiraExt {
        issueStrategy {
            singleIssue('JENKINS-101')
            // - or -
            firstWordOfCommit()
            // - or -
            firstWordOfUpstreamCommit()
            // - or -
            mentionedInCommit()
        }
        jiraOperations {
            transition('Deploy to Test');
            addComment('You went through a Jenkins build!')
            addLabel('Cool stuff')
            updateField('customField_123', 'Hello World')
        }
    }
    //...
  }
}

```
