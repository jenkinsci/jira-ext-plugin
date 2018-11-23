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
import java.util.stream.Collectors;


/**
 * Find JiraCommits by looking for word in the build's changelog. Issues must match in the pattern defined in
 * global config. Also looks in Jira comments for all upstream commits
 *
 * @author milowg
 */
public class MentionedInCommitOrUpstreamCommitsStrategy
        extends MentionedInCommitStrategy
{

    @DataBoundConstructor
    public MentionedInCommitOrUpstreamCommitsStrategy()
    {
        super();
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj != null) && (obj instanceof MentionedInCommitOrUpstreamCommitsStrategy);
    }

    @Override
    public List<JiraCommit> getJiraCommits(AbstractBuild build, BuildListener buildListener)
    {
        List<JiraCommit> jiraCommits = super.getJiraCommits(build, buildListener);

        for (AbstractBuild upstreamBuild : UpstreamBuildUtil.getUpstreamBuilds(build))
        {
            if (jiraCommits.addAll(
                    super.getJiraCommits(upstreamBuild, buildListener)
                            .stream()
                            .filter(jc -> !jiraCommits.contains(jc))
                            .collect(Collectors.toSet())));
        }
        return jiraCommits;
    }

    @Extension
    public static class DescriptorImpl
            extends IssueStrategyExtensionDescriptor
    {

        @Override
        public String getDisplayName()
        {
            return "Mentioned somewhere in commit or upstream build commit message";
        }
    }
}
