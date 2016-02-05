/*
 * Copyright 2005-2016 chemmedia AG
 *
 * You should have received a copy of a license with this program. If not,
 * contact us by visiting http://www.chemmedia.de/ or write to chemmedia AG,
 * Parkstraße 35, 09120 Chemnitz, Germany.
 *
 * You may not use, copy, modify, sublicense, or distribute the Program or any
 * portion of it, except as expressly provided under the given license.
 */
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

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import org.jenkinsci.plugins.jiraext.Config;
import org.jenkinsci.plugins.jiraext.MockChangeLogUtil;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.jvnet.hudson.test.JenkinsRule;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;

import java.util.Arrays;
import java.util.List;


/**
 * TODO DOCUMENT ME!
 *
 * @author wiedsche
 */
public class MentionedInCommitStrategyTest {

    //~ Instance fields --------------------------------------------------------------------------------------

    /**
     * TODO DOCUMENT ME!
     */
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    //~ Methods ----------------------------------------------------------------------------------------------

    /**
     * TODO DOCUMENT ME!
     */
    @Before
    public void setUp() {
        Config.getGlobalConfig().setPattern("FOO-,BAR-");
    }

    /**
     * TODO DOCUMENT ME!
     *
     * @throws Exception
     */
    @Test
    public void testMentionedInCommit() throws Exception {
        MentionedInCommitStrategy strategy = new MentionedInCommitStrategy();

        ChangeLogSet mockChangeSet = MockChangeLogUtil.mockChangeLogSet(new MockChangeLogUtil.MockChangeLog("FOO-101 first",
                                                                                                            "knork"),
                                                                        new MockChangeLogUtil.MockChangeLog("[BAR-102] with square brackets",
                                                                                                            "zark"),
                                                                        new MockChangeLogUtil.MockChangeLog("Fixed FOO-103 inbetween",
                                                                                                            "flarp"),
                                                                        new MockChangeLogUtil.MockChangeLog("Fixed BAR-104typo",
                                                                                                            "narf"),
                                                                        new MockChangeLogUtil.MockChangeLog("FOO-101 again but, because FOO-101 was invalid, but FOO-105 not",
                                                                                                            "knork"),
                                                                        new MockChangeLogUtil.MockChangeLog("Invalid [foo-103] lowercase",
                                                                                                            "flarp"),
                                                                        new MockChangeLogUtil.MockChangeLog("No Valid Ticket",
                                                                                                            "build robot"));
        AbstractBuild mockBuild = mock(AbstractBuild.class);
        when(mockBuild.getChangeSet()).thenReturn(mockChangeSet);

        List<JiraCommit> commits = strategy.getJiraCommits(mockBuild,
                                                           new StreamBuildListener(System.out,
                                                                                   Charset.defaultCharset()));

        assertEquals(commits.size(), 6);

        assertThat(commits, hasItem(Matchers.<JiraCommit>hasProperty("jiraTicket", equalTo("FOO-101"))));
        assertThat(commits, hasItem(Matchers.<JiraCommit>hasProperty("jiraTicket", equalTo("BAR-102"))));
        assertThat(commits, hasItem(Matchers.<JiraCommit>hasProperty("jiraTicket", equalTo("FOO-103"))));
        assertThat(commits, hasItem(Matchers.<JiraCommit>hasProperty("jiraTicket", equalTo("BAR-104"))));
        assertThat(commits, hasItem(Matchers.<JiraCommit>hasProperty("jiraTicket", equalTo("FOO-105"))));
    }

    /**
     * TODO DOCUMENT ME!
     *
     * @throws Exception
     */
    @Test
    public void testSaveConfig() throws Exception {
        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        JiraExtBuildStep builder = new JiraExtBuildStep(new MentionedInCommitStrategy(),
                                                        Arrays.asList((JiraOperationExtension) new AddComment(true,
                                                                                                              "Hello World")));
        project.getBuildersList().add(builder);

        jenkinsRule.submit(jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

        JiraExtBuildStep after = project.getBuildersList().get(JiraExtBuildStep.class);
        jenkinsRule.assertEqualBeans(builder, after, "issueStrategy");
    }
}
