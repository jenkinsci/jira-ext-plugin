/***************************************************************************
 * Copyright (C) 2014 Ping Identity Corporation
 * All rights reserved.
 * <p/>
 * The contents of this file are the property of Ping Identity Corporation.
 * You may not copy or use this file, in either source code or executable
 * form, except in compliance with terms set by Ping Identity Corporation.
 * For further information please contact:
 * <p/>
 * Ping Identity Corporation
 * 1001 17th Street Suite 100
 * Denver, CO 80202
 * 303.468.2900
 * http://www.pingidentity.com
 **************************************************************************/
package org.jenkinsci.plugins.jiraext.view;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Saveable;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.List;

/**
 * @author dalvizu
 */
public class JiraExtPublisherStep
    extends Publisher
{

    public IssueStrategyExtension issueStrategy;

    public DescribableList<JiraOperationExtension, JiraOperationExtensionDescriptor> extensions;

    @DataBoundConstructor
    public JiraExtPublisherStep(IssueStrategyExtension issueStrategy, List<JiraOperationExtension> extensions)
    {
        this.issueStrategy = issueStrategy;
        this.extensions = new DescribableList<>(Saveable.NOOP, Util.fixNull(extensions));
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException
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

    @Override
    public BuildStepMonitor getRequiredMonitorService()
    {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static class DescriptorImpl
        extends Descriptor<Publisher>
    {

        @Override
        public String getDisplayName()
        {
            return "Update JIRA Issues (jira-ext-plugin)";
        }

        public DescriptorExtensionList<IssueStrategyExtension, Descriptor<IssueStrategyExtension>> getIssueStrategies()
        {
            return Jenkins.getInstance().getDescriptorList(IssueStrategyExtension.class);
        }

        public List<JiraOperationExtensionDescriptor> getExtensionDescriptors()
        {
            return JiraOperationExtensionDescriptor.all();
        }
    }
}
