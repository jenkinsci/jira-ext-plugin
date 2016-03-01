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
package org.jenkinsci.plugins.jiraext.svc;

import net.rcarz.jiraclient.JiraException;

import java.util.Map;

/**
 * @author dalvizu
 */
public interface JiraClientSvc
{

    /**
     * Add a comment to a ticket
     * @param jiraIssueKey - the issue key (ex: JENKINS-101)
     * @param comment - the comment to add. Can use JIRA markup
     * @throws JiraException
     */
    void addCommentToTicket(String jiraIssueKey, String comment) throws JiraException;

    /**
     * Add a label to a ticket
     * @param jiraIssueKey - the issue key (ex: JENKINS-101)
     * @param labelToAdd - the label to add
     * @throws JiraException
     */
    void addLabelToTicket(String jiraIssueKey, String labelToAdd) throws JiraException;

    /**
     * Apply the transition to a ticket
     * @param jiraIssueKey - the issue key (ex: JENKINS-101)
     * @param transitionName - the name of the transition
     * @throws JiraException
     */
    void changeWorkflowOfTicket(String jiraIssueKey, String transitionName) throws JiraException;

    void updateField(String jiraTicketNumber, String fieldName, String fieldContent) throws JiraException;

    /**
     * Add a Fix Version to a field, if it doesn't already exist.
     *
     * @param jiraIssueKey
     * @param newFixVersion
     * @throws JiraException if the fix version or issue does not exist, or if there was a problem getting the issue
     */
    void addFixVersion(String jiraIssueKey, String newFixVersion)
            throws JiraException;

    /**
     * Get a map of fieldIds to fieldName for the given issue key
     *
     * @param issueKey
     * @return a map of fieldIds to their fieldNames
     */
    Map<String, String> getJiraFields(String issueKey) throws JiraException;

}
