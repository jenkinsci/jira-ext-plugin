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
 * Add a 'Fix Version' to a JIRA issue. The Fix Version must exist.
 *
 * @author dalvizu
 */
public class AddFixVersion
        extends JiraOperationExtension
{

    public String fixVersion;

    @DataBoundConstructor
    public AddFixVersion(String fixVersion)
    {
        this.fixVersion = fixVersion;
    }

    @Override
    public void perform(List<JiraCommit> commits, AbstractBuild build, Launcher launcher, BuildListener listener)
    {
        for (JiraCommit commit : JiraCommit.filterDuplicateIssues(commits))
        {
            try
            {
                getJiraClientSvc().addFixVersion(commit.getJiraTicket(), fixVersion);
            }
            catch (Throwable t)
            {
                listener.getLogger().println("ERROR Updating fix versions, skipping");
                t.printStackTrace(listener.getLogger());
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof AddFixVersion){
            AddFixVersion other = (AddFixVersion)obj;
            return StringUtils.equals(other.fixVersion, this.fixVersion);
        }
        return false;
    }

    @Extension(optional=true)
    public static class DescriptorImpl
        extends JiraOperationExtensionDescriptor
    {

        @Override
        public String getDisplayName()
        {
            return "Add a Fix Version";
        }
    }
}
