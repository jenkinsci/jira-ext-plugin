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
import hudson.model.FreeStyleProject;
import hudson.model.StreamBuildListener;
import hudson.scm.ChangeLogSet;
import hudson.triggers.SCMTrigger;
import org.jenkinsci.plugins.jiraext.Config;
import org.jenkinsci.plugins.jiraext.MockChangeLogUtil;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author dalvizu
 */
public class MentionedInCommitOrUpstreamCommitStrategyTest
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
        JiraExtBuildStep builder = new JiraExtBuildStep(new MentionedInCommitOrUpstreamCommitsStrategy(),
                Arrays.asList((JiraOperationExtension) new AddComment(true, "Hello World")));
        project.getBuildersList().add(builder);

        jenkinsRule.submit(jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

        JiraExtBuildStep after = project.getBuildersList().get(JiraExtBuildStep.class);
        jenkinsRule.assertEqualBeans(builder, after, "issueStrategy");
    }

    @Test
    public void testNoUpstream()
    {
        MentionedInCommitOrUpstreamCommitsStrategy strategy = new MentionedInCommitOrUpstreamCommitsStrategy();
        List<ChangeLogSet.Entry> validChanges = Arrays.asList(
                MockChangeLogUtil.mockChangeLogSetEntry("Hello World [FOO-101]"),
                MockChangeLogUtil.mockChangeLogSetEntry("FOO-101 ticket at start"),
                MockChangeLogUtil.mockChangeLogSetEntry("In the middle FOO-101 of the message"),
                MockChangeLogUtil.mockChangeLogSetEntry("Weird characters FOO-101: next to it"));
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getChangeSet()).thenReturn(mock(ChangeLogSet.class));
        when(build.getChangeSets()).thenReturn(validChanges);
        when(build.getCauses())
                .thenReturn(Arrays.asList(new SCMTrigger.SCMTriggerCause("Mock a cause")));
        List<JiraCommit> result = strategy.getJiraCommits(build, new StreamBuildListener(System.out, Charset.defaultCharset()));
        assertThat(result.size(), equalTo(4));
    }

}
