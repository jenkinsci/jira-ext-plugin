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
 * Update a Field on an Issue
 *
 * @author dalvizu
 */
public class UpdateField
    extends JiraOperationExtension
{
    private JiraClientSvc jiraClientSvc;

    public String fieldName;

    public String fieldValue;


    @DataBoundConstructor
    public UpdateField(String fieldName, String fieldValue)
    {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    @Inject
    public void setJiraClientSvc(JiraClientSvc jiraClientSvc)
    {
        this.jiraClientSvc = jiraClientSvc;
    }

    @Override
    public void perform(List<JiraCommit> commits, AbstractBuild build, Launcher launcher, BuildListener listener)
    {
        if (jiraClientSvc == null)
        {
            jiraClientSvc = GuiceSingleton.get().getInjector().getInstance(JiraClientSvc.class);
        }
        for (JiraCommit commit : JiraCommit.filterDuplicateIssues(commits))
        {
            try
            {
                jiraClientSvc.updateField(commit.getJiraTicket(), fieldName, fieldValue);
            }
            catch (Throwable t)
            {
                listener.getLogger().println("Error updating ticket, continuing");
                t.printStackTrace(listener.getLogger());
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof UpdateField))
        {
            return false;
        }
        UpdateField other = (UpdateField)obj;
        return StringUtils.equals(fieldValue, other.fieldValue)
                && StringUtils.equals(fieldName, other.fieldName);
    }

    @Override
    public String toString()
    {
        return "UpdateField[fieldName=" + fieldName +
                ",fieldValue=" + fieldValue + "]";
    }

    @Extension
    public static class DescriptorImpl
    extends JiraOperationExtensionDescriptor
    {

        @Override
        public String getDisplayName()
        {
            return "Update a Field";
        }
    }
}
