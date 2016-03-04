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
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractDescribableImpl;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import org.jenkinsci.plugins.jiraext.GuiceSingleton;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.jenkinsci.plugins.jiraext.svc.JiraClientSvc;

import java.util.List;

/**
 * Extension point to add a JiraOperation
 *
 * @author dalvizu
 */
public abstract class JiraOperationExtension
    extends AbstractDescribableImpl<JiraOperationExtension>
{

    @Override
    public Descriptor<JiraOperationExtension> getDescriptor()
    {
        return super.getDescriptor();
    }

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

    public abstract void perform(List<JiraCommit> commits, AbstractBuild build, Launcher launcher, BuildListener listener);
}
