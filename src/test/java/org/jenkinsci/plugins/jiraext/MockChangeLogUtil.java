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
package org.jenkinsci.plugins.jiraext;

import hudson.model.User;
import hudson.plugins.git.GitChangeSet;
import hudson.scm.ChangeLogSet;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author dalvizu
 */
public class MockChangeLogUtil
{

    public static ChangeLogSet mockChangeLogSet(String ... ticketMessages)
    {
        List<MockChangeLog> mockChangeLogs = new ArrayList<>();
        for (String ticketMessage : ticketMessages)
        {
            mockChangeLogs.add(new MockChangeLog(ticketMessage, "dalvizu"));
        }
        return mockChangeLogSet(mockChangeLogs.toArray(new MockChangeLog[mockChangeLogs.size()]));
    }

    /**
     * Mock a GitChangeSet -- the GitChangeSet exposes a one line message in getMessage() and
     * the full text in getComment()
     *
     * @param ticketMessage
     * @param ticketComment
     * @return
     */
    public static ChangeLogSet.Entry mockGitCommit(String ticketMessage, String ticketComment)
    {
        GitChangeSet gitChangeSet = mock(GitChangeSet.class);
        when(gitChangeSet.getMsg()).thenReturn(ticketMessage);
        when(gitChangeSet.getComment()).thenReturn(ticketComment);
        User mockUser = mock(User.class);
        when(mockUser.toString()).thenReturn("dalvizu");
        when(gitChangeSet.getAuthor()).thenReturn(mockUser);
        when(gitChangeSet.getCommitId()).thenReturn("mockCommitId");
        return gitChangeSet;
    }

    public static ChangeLogSet.Entry mockChangeLogSetEntry(String ticketMessage)
    {
        return mockChangeLogSetEntry(new MockChangeLog(ticketMessage, "dalvizu"));
    }

    public static ChangeLogSet.Entry mockChangeLogSetEntry(MockChangeLog mockChangeLog)
    {
        ChangeLogSet.Entry mockChange = mock(ChangeLogSet.Entry.class);
        mock(ChangeLogSet.Entry.class);
        when(mockChange.getMsg()).thenReturn(mockChangeLog.commitMessage);
        User mockUser = mock(User.class);
        when(mockUser.toString()).thenReturn(mockChangeLog.author);
        when(mockChange.getAuthor()).thenReturn(mockUser);
        when(mockChange.getCommitId()).thenReturn("mockCommitId");

        return mockChange;
    }

    public static ChangeLogSet mockChangeLogSet(MockChangeLog ... mockChangeLogs)
    {
        List<ChangeLogSet.Entry> entries = new ArrayList<>();
        for (MockChangeLog mockChangeLog : mockChangeLogs)
        {
            ChangeLogSet.Entry mockChange = mockChangeLogSetEntry(mockChangeLog);
            entries.add(mockChange);
        }
        ChangeLogSet mockChangeLogSet = mock(ChangeLogSet.class);
        when(mockChangeLogSet.iterator()).thenReturn(entries.iterator());
        return mockChangeLogSet;
    }

    public static class MockChangeLog
    {
        String commitMessage;
        String author;

        public MockChangeLog(String commitMessage, String author)
        {
            this.commitMessage = commitMessage;
            this.author = author;
        }
    }
}
