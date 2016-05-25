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

import hudson.model.FreeStyleProject;

import hudson.scm.ChangeLogSet;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import org.jenkinsci.plugins.jiraext.Config;
import org.jenkinsci.plugins.jiraext.MockChangeLogUtil;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.jvnet.hudson.test.JenkinsRule;

import java.util.Arrays;
import java.util.List;


/**
 *
 * @author wiedsche
 */
public class MentionedInCommitStrategyTest
{

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    MentionedInCommitStrategy strategy;

    @Before
    public void setUp()
    {
        Config.getGlobalConfig().setPattern("FOO-,BAR-");
        strategy = new MentionedInCommitStrategy();
    }

    @Test
    public void testSimpleCase()
            throws Exception
    {
        List<ChangeLogSet.Entry> validChanges = Arrays.asList(
                MockChangeLogUtil.mockChangeLogSetEntry("Hello World [FOO-101]"),
                MockChangeLogUtil.mockChangeLogSetEntry("FOO-101 ticket at start"),
                MockChangeLogUtil.mockChangeLogSetEntry("In the middle FOO-101 of the message"),
                MockChangeLogUtil.mockChangeLogSetEntry("Weird characters FOO-101: next to it"));
        for (ChangeLogSet.Entry change : validChanges)
        {
            assertThat(strategy.getJiraIssuesFromChangeSet(change).size(), equalTo(1));
            assertThat(strategy.getJiraIssuesFromChangeSet(change).get(0).getJiraTicket(), equalTo("FOO-101"));
        }

    }

    @Test
    public void testNotFound()
            throws Exception
    {
        ChangeLogSet.Entry entry = MockChangeLogUtil.mockChangeLogSetEntry("SSD-101 test");
        assertThat(strategy.getJiraIssuesFromChangeSet(entry).size(), equalTo(0));
    }

    @Test
    public void testLowerCase()
    {
        ChangeLogSet.Entry entry = MockChangeLogUtil.mockChangeLogSetEntry("foo-101 test");
        assertThat(strategy.getJiraIssuesFromChangeSet(entry).size(), equalTo(0));
    }

    @Test
    public void testMultipleTicketNames()
    {
        ChangeLogSet.Entry entry = MockChangeLogUtil.mockChangeLogSetEntry("FOO-101 and BAR-101 are both in the message");
        assertThat(strategy.getJiraIssuesFromChangeSet(entry).size(), equalTo(2));
        assertThat(strategy.getJiraIssuesFromChangeSet(entry),
                hasItem(Matchers.<JiraCommit>hasProperty("jiraTicket", equalTo("FOO-101"))));
        assertThat(strategy.getJiraIssuesFromChangeSet(entry),
                hasItem(Matchers.<JiraCommit>hasProperty("jiraTicket", equalTo("BAR-101"))));
    }

    @Test
    public void testInvalid()
    {
        ChangeLogSet.Entry entry = MockChangeLogUtil.mockChangeLogSetEntry("Fixed BAR-104typo");
        assertThat(strategy.getJiraIssuesFromChangeSet(entry).size(), equalTo(1));
    }

    @Test
    public void testTicketRepeatsItself()
    {
        ChangeLogSet.Entry entry = MockChangeLogUtil.mockChangeLogSetEntry("FOO-101 is in the message twice FOO-101");
        assertThat(strategy.getJiraIssuesFromChangeSet(entry).size(), equalTo(1));
        assertThat(strategy.getJiraIssuesFromChangeSet(entry),
                hasItem(Matchers.<JiraCommit>hasProperty("jiraTicket", equalTo("FOO-101"))));
    }

    @Test
    public void testSaveConfig()
            throws Exception
    {
        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        JiraExtBuildStep builder = new JiraExtBuildStep(new MentionedInCommitStrategy(),
                Arrays.asList((JiraOperationExtension) new AddComment(true, "Hello World")));
        project.getBuildersList().add(builder);

        jenkinsRule.submit(jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

        JiraExtBuildStep after = project.getBuildersList().get(JiraExtBuildStep.class);
        jenkinsRule.assertEqualBeans(builder, after, "issueStrategy");
    }

    @Test
    public void testJenkins33856()
    {
        ChangeLogSet.Entry entry = MockChangeLogUtil.mockChangeLogSetEntry("Testing JIRA (ticket with a few spaces at the end and no EOL) FOO-141   ");
        assertThat(strategy.getJiraIssuesFromChangeSet(entry).size(), equalTo(1));
        assertThat(strategy.getJiraIssuesFromChangeSet(entry),
                hasItem(Matchers.<JiraCommit>hasProperty("jiraTicket", equalTo("FOO-141"))));
    }
}
