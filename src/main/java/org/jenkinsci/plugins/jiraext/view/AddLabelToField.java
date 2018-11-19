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
import hudson.util.FormValidation;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.jiraext.Config;
import org.jenkinsci.plugins.jiraext.GuiceSingleton;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.jenkinsci.plugins.jiraext.svc.JiraClientSvc;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.ForwardToView;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Add a label to a field on an issue
 */

public class AddLabelToField
        extends JiraOperationExtension
{
    public String fieldName;

    public String fieldValue;

    private static final Logger logger = Logger.getLogger(AddLabelToField.class.getSimpleName());

    @DataBoundConstructor
    public AddLabelToField(String fieldName, String fieldValue)
    {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    @Override
    public void perform(List<JiraCommit> commits, AbstractBuild build, Launcher launcher, BuildListener listener)
    {
        for (JiraCommit commit : JiraCommit.filterDuplicateIssues(commits))
        {
            try
            {
                String expandedValue = build.getEnvironment(listener).expand(fieldValue);
                getJiraClientSvc().addLabelToField(commit.getJiraTicket(), fieldName, expandedValue);
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
        if (obj == null || !(obj instanceof AddLabelToField))
        {
            return false;
        }
        AddLabelToField other = (AddLabelToField)obj;
        return StringUtils.equals(fieldValue, other.fieldValue)
                && StringUtils.equals(fieldName, other.fieldName);
    }

    @Override
    public String toString()
    {
        return "AddLabelToField[fieldName=" + fieldName +
                ",fieldValue=" + fieldValue + "]";
    }

    @Extension
    public static class DescriptorImpl
            extends JiraOperationExtensionDescriptor
    {

        private transient JiraClientSvc jiraClientSvc;

        public synchronized final void setJiraClientSvc(JiraClientSvc jiraClientSvc)
        {
            this.jiraClientSvc = jiraClientSvc;
        }

        public synchronized final JiraClientSvc getJiraClientSvc()
        {
            if (jiraClientSvc == null)
            {
                jiraClientSvc = new GuiceSingleton().getInjector().getInstance(JiraClientSvc.class);
            }
            return jiraClientSvc;
        }

        public HttpResponse doQueryJiraFields(@QueryParameter String issueKey)
        {
            try
            {
                if (!Config.getGlobalConfig().isJiraConfigComplete())
                {
                    return FormValidation.error("JIRA settings are not set in global config");
                }
                final Map<String, String> jiraFields = getJiraClientSvc().getJiraFields(issueKey);
                return new ForwardToView(this, "/org/jenkinsci/plugins/jiraext/view/AddLabelToField/jiraFields.jelly")
                        .with("jiraFieldMap", jiraFields);
            }
            catch (Throwable t)
            {
                String message =  "Error finding FieldIds for issueKey: " + issueKey;
                logger.log(Level.WARNING, message, t);
                return FormValidation.error(t, message);
            }
        }

        @Override
        public String getDisplayName()
        {
            return "Add a label to a field";
        }
    }
}
