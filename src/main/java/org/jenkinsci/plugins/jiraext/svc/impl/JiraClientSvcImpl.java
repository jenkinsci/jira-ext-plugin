/***************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 **************************************************************************/
package org.jenkinsci.plugins.jiraext.svc.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.rcarz.jiraclient.Field;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.apache.commons.lang.Validate;
import org.jenkinsci.plugins.jiraext.svc.JiraClientFactory;
import org.jenkinsci.plugins.jiraext.svc.JiraClientSvc;

/**
 * @author dalvizu
 */
@Singleton
public final class JiraClientSvcImpl
    implements JiraClientSvc
{

    private JiraClientFactory jiraClientFactory;

    private JiraClient newJiraClient()
    {
        if (jiraClientFactory == null)
        {
            throw new RuntimeException("Service not injected correctly wtf");
        }

        return jiraClientFactory.newJiraClient();
    }

    @Inject
    public void setJiraClientFactory( JiraClientFactory jiraClientFactory)
    {
        this.jiraClientFactory = jiraClientFactory;
    }

    @Override
    public void addCommentToTicket(String jiraTicketNumber, String comment) throws JiraException
    {
        JiraClient client = newJiraClient();
        client.getIssue(jiraTicketNumber).addComment(comment);
    }

    @Override
    public void addLabelToTicket(String jiraTicketNumber, String labelsToAdd) throws JiraException
    {
        JiraClient client = newJiraClient();
        Issue issue = client.getIssue(jiraTicketNumber);
        for (String labelToAdd : labelsToAdd.split(" "))
        {
            if (!issue.getLabels().contains(labelToAdd))
            {
                issue.update().fieldAdd(Field.LABELS, labelToAdd).execute();
            }
        }
    }

    @Override
    public void changeWorkflowOfTicket(String jiraTicketNumber, String transitionName) throws JiraException
    {
        JiraClient jiraClient = newJiraClient();
        Issue issue = jiraClient.getIssue(jiraTicketNumber);
        issue.transition().execute(transitionName);
    }

    @Override
    public void updateField(String jiraTicketNumber, String jiraFieldName,
                            String content)
            throws JiraException
    {
        JiraClient client = newJiraClient();
        Issue issue = client.getIssue(jiraTicketNumber);
        Validate.notNull(issue);
        issue.update().field(jiraFieldName, content).execute();
    }
}
