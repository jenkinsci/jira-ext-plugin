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

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/**
 * @author dalvizu
 */
public class JiraExtBuildStep
    extends Builder
{

    public IssueStrategyExtension issueStrategy;

    public DescribableList<JiraOperationExtension, JiraOperationExtensionDescriptor> extensions;

    @DataBoundConstructor
    public JiraExtBuildStep(IssueStrategyExtension issueStrategy, List<JiraOperationExtension> extensions)
    {
        this.issueStrategy = issueStrategy;
        this.extensions = new DescribableList<>(Saveable.NOOP, Util.fixNull(extensions));
    }

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener)
    {
        listener.getLogger().println("Updating JIRA tickets");
        List<JiraCommit> commits = issueStrategy.getJiraCommits(build, listener);
        for (JiraOperationExtension extension : extensions)
        {
            listener.getLogger().println("Operation: " + extension.getDescriptor().getDisplayName());
            extension.perform(commits, build, launcher, listener);
        }
        listener.getLogger().println("Finish updating JIRA tickets");
        return true;
    }

    /**
     * All the configured extensions attached to this {@link JiraExtBuildStep}.
     */
    public DescribableList<JiraOperationExtension, JiraOperationExtensionDescriptor> getExtensions() {
        return extensions;
    }

    @Extension
    public static class DescriptorImpl
        extends BuildStepDescriptor<Builder> {

        @Override
        public String getDisplayName()
        {
            return "Update JIRA Issues (jira-ext-plugin)";
        }

        public DescriptorExtensionList<IssueStrategyExtension, Descriptor<IssueStrategyExtension>> getIssueStrategies()
        {
            return Jenkins.get().getDescriptorList(IssueStrategyExtension.class);
        }

        public List<JiraOperationExtensionDescriptor> getExtensionDescriptors()
        {
            return JiraOperationExtensionDescriptor.all();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // This builder can be used with all kinds of project types
            return true;
        }
    }
}
