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

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.model.StreamBuildListener;
import org.jenkinsci.plugins.jiraext.MockChangeLogUtil;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author dalvizu
 */
public class JiraPublisherStepTest
{

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testSaveConfig()
            throws Exception
    {
        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        JiraExtPublisherStep publisher = new JiraExtPublisherStep(new FirstWordOfCommitStrategy(),
                Arrays.asList((JiraOperationExtension) new AddComment(true, "Hello World")));
        project.getPublishersList().add(publisher);

        jenkinsRule.submit(jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

        JiraExtPublisherStep after = project.getPublishersList().get(JiraExtPublisherStep.class);
        jenkinsRule.assertEqualBeans(publisher, after, "issueStrategy,extensions");
    }

    @Test
    public void testInvokeOperations()
            throws Exception
    {
        IssueStrategyExtension mockStrategy = mock(IssueStrategyExtension.class);
        JiraOperationExtension mockOperation = mock(JiraOperationExtension.class);
        Descriptor mockDescriptor = mock(Descriptor.class);
        when(mockDescriptor.getDisplayName()).thenReturn("Mock descriptor");
        when(mockOperation.getDescriptor()).thenReturn(mockDescriptor);
        JiraExtPublisherStep publisher = new JiraExtPublisherStep(mockStrategy,
                Arrays.asList(mockOperation));
        List<JiraCommit> commits = Arrays.asList(new JiraCommit("JENKINS-101",
                MockChangeLogUtil.mockChangeLogSetEntry("example ticket")));

        when(mockStrategy.getJiraCommits(any(AbstractBuild.class), any(BuildListener.class)))
                .thenReturn(commits);

        assertTrue(publisher.perform(mock(AbstractBuild.class), mock(Launcher.class),
                new StreamBuildListener(System.out, Charset.defaultCharset())));
        verify(mockOperation).perform(eq(commits), any(AbstractBuild.class), any(Launcher.class), any(BuildListener.class));
    }

}
