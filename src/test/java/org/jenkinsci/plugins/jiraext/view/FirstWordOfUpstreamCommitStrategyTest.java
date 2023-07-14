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
import org.jenkinsci.plugins.jiraext.JiraExtConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Arrays;

/**
 * @author dalvizu
 */
public class FirstWordOfUpstreamCommitStrategyTest
{
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Before
    public void setUp()
    {
        JiraExtConfig.getGlobalConfig().setPattern("FOO-,BAR-");
    }

    @Test
    public void testSaveConfig()
            throws Exception
    {
        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        JiraExtBuildStep builder = new JiraExtBuildStep(new FirstWordOfUpstreamCommitStrategy(),
                Arrays.asList((JiraOperationExtension) new AddComment(true, "Hello World")));
        project.getBuildersList().add(builder);

        jenkinsRule.submit(jenkinsRule.createWebClient().getPage(project, "configure").getFormByName("config"));

        JiraExtBuildStep after = project.getBuildersList().get(JiraExtBuildStep.class);
        jenkinsRule.assertEqualBeans(builder, after, "issueStrategy");
    }

}
