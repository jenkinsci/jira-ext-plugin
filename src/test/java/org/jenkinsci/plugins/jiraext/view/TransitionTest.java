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
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.FreeStyleProject;
import hudson.model.StreamBuildListener;
import hudson.model.TaskListener;
import net.rcarz.jiraclient.JiraException;
import org.jenkinsci.plugins.jiraext.MockChangeLogUtil;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.jenkinsci.plugins.jiraext.svc.JiraClientSvc;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

/**
 * @author dalvizu
 */
public class TransitionTest
{
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    public JiraClientSvc jiraClientSvc;

    @Before
    public void setup()
    {
        jiraClientSvc = mock(JiraClientSvc.class);
    }

    @Test
    public void testSaveConfig()
            throws Exception
    {
        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        Transition transition = new Transition("Resolve");
        JiraExtBuildStep builder = new JiraExtBuildStep(new SingleTicketStrategy("JENKINS-101"),
                Arrays.asList((JiraOperationExtension) transition));
        project.getBuildersList().add(builder);
        jenkinsRule.submit(jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));
        JiraExtBuildStep after = project.getBuildersList().get(JiraExtBuildStep.class);
        jenkinsRule.assertEqualBeans(builder, after, "extensions");
    }

    @Test
    public void testTransition()
            throws Exception
    {
        Transition transition = new Transition("Resolve");
        transition.setJiraClientSvc(jiraClientSvc);
        AbstractBuild mockBuild = mock(AbstractBuild.class);
        when(mockBuild.getEnvironment(any(TaskListener.class))).thenReturn(new EnvVars());
        List<JiraCommit> jiraCommits = new ArrayList<>();
        jiraCommits.add(new JiraCommit("SSD-101", MockChangeLogUtil.mockChangeLogSetEntry("Test Comment")));
        jiraCommits.add(new JiraCommit("SSD-101", MockChangeLogUtil.mockChangeLogSetEntry("Test Comment")));

        transition.perform(jiraCommits, mockBuild, mock(Launcher.class), new StreamBuildListener(System.out, Charset.defaultCharset()));
        verify(jiraClientSvc, times(1)).changeWorkflowOfTicket(eq("SSD-101"), eq("Resolve"));
    }

    /**
     * Test multiple transitions when some are invalid.
     */
    @Test
    public void testMultipleTransition()
            throws Exception
    {
        Transition transition = new Transition("Develop,Resolve,Closed");
        transition.setJiraClientSvc(jiraClientSvc);
        AbstractBuild mockBuild = mock(AbstractBuild.class);
        when(mockBuild.getEnvironment(any(TaskListener.class))).thenReturn(new EnvVars());

        doThrow(new JiraException("Invalid Transition")).when(jiraClientSvc).changeWorkflowOfTicket(eq("SSD-101"), eq("Develop"));

        List<JiraCommit> jiraCommits = new ArrayList<>();
        jiraCommits.add(new JiraCommit("SSD-101", MockChangeLogUtil.mockChangeLogSetEntry("Test Comment")));

        transition.perform(jiraCommits, mockBuild, mock(Launcher.class), new StreamBuildListener(System.out, Charset.defaultCharset()));
        verify(jiraClientSvc, times(2)).changeWorkflowOfTicket(eq("SSD-101"), eq("Develop"));
        verify(jiraClientSvc, times(1)).changeWorkflowOfTicket(eq("SSD-101"), eq("Resolve"));
        verify(jiraClientSvc, times(1)).changeWorkflowOfTicket(eq("SSD-101"), eq("Closed"));
    }

    /**
     * An exception adding the first label add should not disrupt the next label
     */
    @Test
    public void testResiliency()
            throws Exception
    {
        Transition transition = new Transition("Resolve");
        transition.setJiraClientSvc(jiraClientSvc);

        AbstractBuild mockBuild = mock(AbstractBuild.class);
        when(mockBuild.getEnvironment(any(TaskListener.class))).thenReturn(new EnvVars());
        List<JiraCommit> jiraCommits = new ArrayList<>();
        jiraCommits.add(new JiraCommit("SSD-101", MockChangeLogUtil.mockChangeLogSetEntry("Test Comment")));
        jiraCommits.add(new JiraCommit("SSD-102", MockChangeLogUtil.mockChangeLogSetEntry("Test Comment")));
        doThrow(new RuntimeException("Issue is invalid"))
                .when(jiraClientSvc).changeWorkflowOfTicket("SSD-101", "Test Comment");
        transition.perform(jiraCommits, mockBuild, mock(Launcher.class), new StreamBuildListener(System.out, Charset.defaultCharset()));
        verify(jiraClientSvc, times(1)).changeWorkflowOfTicket(eq("SSD-101"), eq("Resolve"));
        verify(jiraClientSvc, times(1)).changeWorkflowOfTicket(eq("SSD-102"), eq("Resolve"));
    }
}
