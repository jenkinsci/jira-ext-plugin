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

import com.google.inject.Inject;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.jiraext.GuiceSingleton;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.jenkinsci.plugins.jiraext.svc.JiraClientSvc;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/**
 * Transition a JIRA issue
 *
 * @author dalvizu
 */
public class Transition
    extends JiraOperationExtension
{

    public String transitionName;

    private JiraClientSvc jiraClientSvc;

    @DataBoundConstructor
    public Transition(String transitionName)
    {
        this.transitionName = transitionName;
    }

    @Inject
    public void setJiraClientSvc(JiraClientSvc jiraClientSvc)
    {
        this.jiraClientSvc = jiraClientSvc;
    }

    @Override
    public void perform(List<JiraCommit> jiraCommitList,
                        AbstractBuild build, Launcher launcher, BuildListener listener)
    {
        if (jiraClientSvc == null)
        {
            jiraClientSvc = new GuiceSingleton().getInjector().getInstance(JiraClientSvc.class);
        }
        try
        {
            for (JiraCommit jiraCommit : JiraCommit.filterDuplicateIssues(jiraCommitList))
            {
                listener.getLogger().println("Transition a ticket: " + jiraCommit.getJiraTicket());
                listener.getLogger().println("transitionName: " + transitionName);
                try
                {
                    jiraClientSvc.changeWorkflowOfTicket(jiraCommit.getJiraTicket(), transitionName);
                }
                catch (Throwable t)
                {
                    listener.getLogger().println("ERROR Updating JIRA, continuing");
                    t.printStackTrace(listener.getLogger());
                }
            }
        }
        catch (Throwable t)
        {
            listener.getLogger().println("ERROR Updating JIRA");
            t.printStackTrace(listener.getLogger());
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof Transition))
        {
            return false;
        }
        Transition other = (Transition)obj;
        return StringUtils.equals(transitionName, other.transitionName);
    }

    @Override
    public String toString()
    {
        return "Transition[transitionName=" + transitionName +"]";
    }
    @Extension
    public static class DescriptorImpl
        extends JiraOperationExtensionDescriptor {

        @Override
        public String getDisplayName()
        {
            return "Transition tickets";
        }
    }
}
