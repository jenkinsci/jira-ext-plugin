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
 * Add a label to a JIRA issue
 *
 * @author dalvizu
 */
public class AddLabel
    extends JiraOperationExtension
{

    public String labelName;

    @DataBoundConstructor
    public AddLabel(String labelName)
    {
        this.labelName = labelName;
    }

    @Override
    public void perform(List<JiraCommit> jiraCommitList,
                        AbstractBuild build, Launcher launcher, BuildListener listener)
    {
        for (JiraCommit jiraCommit : JiraCommit.filterDuplicateIssues(jiraCommitList))
        {
            listener.getLogger().println("Add label to ticket: " + jiraCommit.getJiraTicket());
            listener.getLogger().println("Label: " + labelName);
            try
            {
                String expandedName = build.getEnvironment(listener).expand(labelName);
                getJiraClientSvc().addLabelToTicket(jiraCommit.getJiraTicket(), expandedName);
            }
            catch (Throwable t)
            {
                listener.getLogger().println("ERROR Updating jira notifications");
                t.printStackTrace(listener.getLogger());
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof AddLabel))
        {
            return false;
        }
        AddLabel other = (AddLabel)obj;
        return StringUtils.equals(labelName, other.labelName);
    }

    @Override
    public String toString()
    {
        return "AddLabel[labelName=" + labelName + "]";
    }

    @Extension
    public static class DescriptorImpl
        extends JiraOperationExtensionDescriptor {

        @Override
        public String getDisplayName()
        {
            return "Add a label";
        }
    }
}
