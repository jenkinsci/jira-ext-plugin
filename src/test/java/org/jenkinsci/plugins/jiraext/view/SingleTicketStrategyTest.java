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

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.model.StreamBuildListener;
import org.jenkinsci.plugins.jiraext.Config;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author dalvizu
 */
public class SingleTicketStrategyTest
{
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Before
    public void setUp()
    {
        Config.getGlobalConfig().setPattern("FOO-,BAR-");
    }

    @Test
    public void testSaveConfig()
            throws Exception
    {
        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        JiraExtBuildStep builder = new JiraExtBuildStep(new SingleTicketStrategy("FOO-1"),
                Arrays.asList((JiraOperationExtension) new AddComment(true, "Hello World")));
        project.getBuildersList().add(builder);

        jenkinsRule.submit(jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

        JiraExtBuildStep after = project.getBuildersList().get(JiraExtBuildStep.class);
        jenkinsRule.assertEqualBeans(builder, after, "issueStrategy");
    }

    @Test
    public void testExpansion()
            throws Exception
    {
        AbstractBuild mockBuild = mock(AbstractBuild.class);
        EnvVars envVars = new EnvVars();
        envVars.put("FOO", "BAR");
        when(mockBuild.getEnvironment(any(BuildListener.class))).thenReturn(envVars);
        SingleTicketStrategy strategy = new SingleTicketStrategy("$FOO");
        List<JiraCommit> commits = strategy.getJiraCommits(mockBuild, mock(BuildListener.class));
        assertEquals(1, commits.size());
        assertEquals("BAR", commits.get(0).getJiraTicket());
    }

    @Test
    public void testErrorInExpansion()
            throws Exception
    {
        AbstractBuild mockBuild = mock(AbstractBuild.class);
        SingleTicketStrategy strategy = new SingleTicketStrategy("$FOO");
        when(mockBuild.getEnvironment(any(BuildListener.class))).thenThrow(new IOException());
        List<JiraCommit> commits = strategy.getJiraCommits(mockBuild,
                new StreamBuildListener(System.out, Charset.defaultCharset()));
        assertEquals(1, commits.size());
        assertEquals("$FOO", commits.get(0).getJiraTicket());

    }
}
