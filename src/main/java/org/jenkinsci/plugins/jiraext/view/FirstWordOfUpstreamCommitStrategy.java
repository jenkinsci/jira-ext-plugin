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

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.jenkinsci.plugins.jiraext.UpstreamBuildUtil;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/**
 * Find JiraCommits by looking for the first word in the build's upstream
 * changelog. Issues must match in the pattern defined in global config.
 *
 * For example, if prefix is set to 'JENKINS-,FOO-'
 *
 * And a commit had the message 'JENKINS-101 fixed stuff', this would return
 * 'JENKINS-101' as a JiraCommit.
 *
 * @author dalvizu
 */
public class FirstWordOfUpstreamCommitStrategy
    extends IssueStrategyExtension
{

    @DataBoundConstructor
    public FirstWordOfUpstreamCommitStrategy()
    {
        super();
    }

    @Override
    public List<JiraCommit> getJiraCommits(AbstractBuild build, BuildListener buildListener)
    {
        return new FirstWordOfCommitStrategy().getJiraCommits(UpstreamBuildUtil.getUpstreamBuild(build),
                buildListener);
    }

    @Extension
    public static class DescriptorImpl
            extends IssueStrategyExtensionDescriptor
    {

        @Override
        public String getDisplayName()
        {
            return "First word upstream commit";
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj != null && obj instanceof FirstWordOfUpstreamCommitStrategy;
    }

    @Override
    public int hashCode()
    {
        return FirstWordOfUpstreamCommitStrategy.class.hashCode();
    }
}
