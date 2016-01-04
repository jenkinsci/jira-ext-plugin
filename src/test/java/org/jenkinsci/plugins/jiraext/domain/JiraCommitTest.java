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

import org.jenkinsci.plugins.jiraext.MockChangeLogUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author dalvizu
 */
public class JiraCommitTest
{
    @Test
    public void testNullFilter()
    {
        List<JiraCommit> commits = JiraCommit.filterDuplicateIssues(null);
        assertNotNull(commits);
        assertEquals(0, commits.size());
    }

    @Test
    public void testFilter()
    {
        JiraCommit firstCommit = new JiraCommit("FOO-1");
        JiraCommit secondCommit = new JiraCommit("FOO-1");
        List<JiraCommit> commits = JiraCommit.filterDuplicateIssues(Arrays.asList(firstCommit, secondCommit));
        assertEquals(1, commits.size());
        assertEquals("FOO-1", commits.get(0).getJiraTicket());
    }

    @Test
    public void testEquals()
    {
        JiraCommit commit = new JiraCommit("FOO-1");
        assertFalse(commit.equals(null));
        assertTrue(commit.equals(commit));
        assertTrue(commit.equals(new JiraCommit("FOO-1")));

        JiraCommit commitWithChangeLog = new JiraCommit("FOO-1", MockChangeLogUtil.mockChangeLogSetEntry("FOO-1"));
        assertTrue(commitWithChangeLog.equals(commitWithChangeLog));
        assertFalse(commitWithChangeLog
                .equals(new JiraCommit("FOO-1", MockChangeLogUtil.mockChangeLogSetEntry("Something else"))));
    }

}
