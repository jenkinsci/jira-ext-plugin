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
package org.jenkinsci.plugins.jiraext.view;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.scm.ChangeLogSet;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An IssueStrategyExtension which assumes you want to return a list of jira commits by
 * deriving them from the provided build's {@link ChangeLogSet}. Also includes upstream build ChangeLogSets
 *
 * @author dalvizu
 */
public abstract class AbstractParsingIssueStrategy
    extends IssueStrategyExtension
{
    private static final Logger _logger = Logger.getLogger(FirstWordOfCommitStrategy.class.getName());

    @Override
    public List<JiraCommit> getJiraCommits(AbstractBuild build,
                                           BuildListener listener)
    {
        List<JiraCommit> result = new ArrayList<>();

        try
        {
            _logger.log(Level.FINE, "iterateTicketsAndApply");
            String projectName = build.getProject() == null ? "" : build.getProject().getName();
            Integer buildNumber = build.getNumber();
            listener.getLogger().println(String.format("ChangeLogSet from %s build %d, class: %s", projectName,
                    buildNumber, build.getChangeSet().getClass()));

            for (Object entry : build.getChangeSets())
            {
                try
                {
                    ChangeLogSet.Entry change = (ChangeLogSet.Entry) entry;
                    _logger.log(Level.FINE, "Found commit: " + (change == null ? "null" : change.getCommitId()));
                    List<JiraCommit> changes = getJiraIssuesFromChangeSet(change);
                    if (changes != null)
                    {
                        result.addAll(changes);
                    }
                    else
                    {
                        listener.getLogger().println("Unable to find a JIRA ticket in the message: " + change.getMsg());
                    }
                }
                catch (Exception e)
                {
                    listener.getLogger().println("ERROR Updating jira notifications");
                    e.printStackTrace(listener.getLogger());
                }
            }
        } catch (Exception e)
        {
            listener.getLogger().println("ERROR Updating jira notifications");
            e.printStackTrace(listener.getLogger());
        }
        return result;
    }

    /**
     * Parse a JIRA issue key, ie SSD-101, out of the given ChangeLogSet.Entry.
     *
     * @param change
     * @return
     */
    protected abstract List<JiraCommit> getJiraIssuesFromChangeSet(ChangeLogSet.Entry change);

}
