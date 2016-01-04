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
package org.jenkinsci.plugins.jiraext.domain;

import com.google.common.base.Optional;
import hudson.scm.ChangeLogSet;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dalvizu
 */
public class JiraCommit
{
    private String jiraTicket;

    private Optional<ChangeLogSet.Entry> changeSet;

    public JiraCommit(String jiraTicket)
    {
        this(jiraTicket, null);
    }

    public JiraCommit(String jiraTicket, ChangeLogSet.Entry changeSet)
    {
        this.jiraTicket = jiraTicket;
        if (changeSet == null)
        {
            this.changeSet = Optional.absent();
        }
        else
        {
            this.changeSet = Optional.of(changeSet);
        }
    }

    public String getJiraTicket()
    {
        return jiraTicket;
    }

    public Optional<ChangeLogSet.Entry> getChangeSet()
    {
        return changeSet;
    }

    /**
     * Remove issues with the same Issue key
     * @param jiraCommitList list of commits to filter
     * @return a list with no two issues with the same Issue key
     */
    public static List<JiraCommit> filterDuplicateIssues(List<JiraCommit> jiraCommitList)
    {
        if (jiraCommitList == null)
        {
            return new ArrayList<>();
        }
        // only post once for every ticket
        List<JiraCommit> filteredCommits = new ArrayList<>();
        List<String> alreadyAddedIssues = new ArrayList<>();
        for (JiraCommit commit : jiraCommitList)
        {
            if (!alreadyAddedIssues.contains(commit.getJiraTicket()))
            {
                alreadyAddedIssues.add(commit.getJiraTicket());
                filteredCommits.add(commit);
            }
        }

        return filteredCommits;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof JiraCommit))
        {
            return false;
        }
        JiraCommit other = (JiraCommit)obj;
        return (StringUtils.equals(jiraTicket, other.jiraTicket)
                && ObjectUtils.equals(changeSet, other.changeSet));
    }
}
