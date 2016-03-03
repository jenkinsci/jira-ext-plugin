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
import net.rcarz.jiraclient.Project;
import net.rcarz.jiraclient.RestClient;
import net.rcarz.jiraclient.Version;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.Validate;
import org.jenkinsci.plugins.jiraext.svc.JiraClientFactory;
import org.jenkinsci.plugins.jiraext.svc.JiraClientSvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author dalvizu
 */
@Singleton
public final class JiraClientSvcImpl
    implements JiraClientSvc
{

    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    private JiraClientFactory jiraClientFactory;

    @Override
    public JiraClient newJiraClient()
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
    public void addCommentToTicket(String jiraIssueKey, String comment) throws JiraException
    {
        logger.fine("Add comment to ticket: " + jiraIssueKey + " comment: " + comment);
        JiraClient client = newJiraClient();
        client.getIssue(jiraIssueKey).addComment(comment);
    }

    @Override
    public void addLabelToTicket(String jiraIssueKey, String labelsToAdd) throws JiraException
    {
        logger.fine("Add label to ticket: " + jiraIssueKey + " label: " + labelsToAdd);

        JiraClient client = newJiraClient();
        Issue issue = client.getIssue(jiraIssueKey);
        for (String labelToAdd : labelsToAdd.split(" "))
        {
            if (issue.getLabels().contains(labelToAdd))
            {
                logger.fine("Label already exists on ticket, skipping");
            }
            else
            {
                logger.fine("Adding label: " + labelToAdd);
                issue.update().fieldAdd(Field.LABELS, labelToAdd).execute();
            }
        }
    }

    @Override
    public void changeWorkflowOfTicket(String jiraIssueKey, String transitionName) throws JiraException
    {
        logger.fine("Transition ticket: " + jiraIssueKey + " transition name: " + transitionName);
        JiraClient jiraClient = newJiraClient();
        Issue issue = jiraClient.getIssue(jiraIssueKey);
        issue.transition().execute(transitionName);
    }

    @Override
    public void updateStringField(String jiraIssueKey, String jiraFieldName,
                            String content)
            throws JiraException
    {
        logger.fine("Update ticket: " + jiraIssueKey + " field name: " + jiraFieldName + " with content " + content);

        JiraClient client = newJiraClient();
        Issue issue = client.getIssue(jiraIssueKey);
        Validate.notNull(issue);
        issue.update().field(jiraFieldName, content).execute();
    }

    @Override
    public void updateMultiSelectField(String jiraIssueKey, String jiraFieldName, String... values)
            throws JiraException
    {
        logger.fine("Update ticket: " + jiraIssueKey + " field name: " + jiraFieldName + " with values " + values);

        JiraClient client = newJiraClient();
        Issue issue = client.getIssue(jiraIssueKey);
        Validate.notNull(issue);
        RestClient restClient = client.getRestClient();
        try
        {
            JSONObject payload = new JSONObject();
            JSONObject fields = new JSONObject();
            JSONArray valueList = new JSONArray();
            for (String value : values)
            {
                JSONObject shittyApi = new JSONObject();
                shittyApi.put("value", value);
                valueList.add(shittyApi);
            }
            fields.put(jiraFieldName, valueList);
            payload.put("fields", fields);
            restClient.put("/rest/api/latest/issue/" + jiraIssueKey, payload);
        }
        catch (Throwable t)
        {
            String msg = "Error updating multi-select issue field";
            logger.log(Level.WARNING, msg, t);
            throw new JiraException(msg, t);
        }
    }

    @Override
    public void addFixVersion(String jiraIssueKey, String newFixVersion)
            throws JiraException
    {
        logger.fine("Add fix version " + newFixVersion + " to ticket: " + jiraIssueKey);
        JiraClient client = newJiraClient();
        Issue issue = client.getIssue(jiraIssueKey);
        Validate.notNull(issue);
        List<Version> existingVersions = issue.getFixVersions();
        for (Version version : existingVersions)
        {
            logger.fine("found version: " + version.getName() + " id: " + version.getId());
            if (version.getName().equals(newFixVersion))
            {
                logger.fine("Fix version is already on the ticket, skipping");
                return;
            }
        }
        Project project = issue.getProject();
        Validate.notNull(project);
        logger.fine("Finding project with key: " + project.getKey());
        project = Project.get(client.getRestClient(), project.getKey());
        Validate.notNull(project);
        List<Version> projectVersions = project.getVersions();
        Version newVersion = getVersion(projectVersions, newFixVersion);
        existingVersions.add(newVersion);
        issue.update().field(Field.FIX_VERSIONS, existingVersions).execute();
    }

    @Override
    public Map<String, String> getJiraFields(String issueKey)
            throws JiraException
    {
        logger.fine("Get JIRA field ids for key: " + issueKey);
        Map<String, String> result = new HashMap<>();
        JiraClient client = newJiraClient();
        RestClient restClient = client.getRestClient();
        try {
            JSONObject json = (JSONObject)restClient.get("/rest/api/latest/issue/" + issueKey);
            JSONObject fields = (JSONObject) json.get("fields");

            for (Object key : fields.keySet())
            {
                String fieldName = (String)key;
                String fieldValue = "null";
                if (fields.get(fieldName) != null)
                {
                    fieldValue = fields.get(fieldName).toString();
                }
                result.put(fieldName, fieldValue);
            }

            return result;
        }
        catch (Throwable t) {
            throw new JiraException("Exception getting fields for JIRA issue", t);
        }
    }

    @Override
    public Object getFieldValue(String jiraTicket, String jiraFieldId)
            throws JiraException
    {
        return newJiraClient().getIssue(jiraTicket).getField(jiraFieldId);
    }

    /**
     *
     * @param projectVersions - list of Versions to look for a name
     * @param name - the version.name to look for
     * @return the first Version with the given name
     * @throws JiraException when no Version with that name exists
     */
    private Version getVersion(List<Version> projectVersions, String name)
        throws JiraException
    {
        List<String> foundVersionNames = new ArrayList();
        for (Version version : projectVersions)
        {
            if (name.equals(version.getName()))
            {
                foundVersionNames.add(version.getName());
                return version;
            }
        }
        throw new JiraException("Unable to find version with name: " + name + ", only found versions named: " + foundVersionNames);
    }
}
