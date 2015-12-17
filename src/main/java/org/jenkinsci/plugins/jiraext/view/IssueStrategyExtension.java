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

import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.AbstractDescribableImpl;
import hudson.model.BuildListener;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;

import java.util.List;

/**
 * Extension point for gathering a list of JiraCommits from a given build
 *
 * @author dalvizu
 */
public abstract class IssueStrategyExtension
    extends AbstractDescribableImpl<IssueStrategyExtension>
    implements ExtensionPoint
{
    public abstract List<JiraCommit> getJiraCommits(AbstractBuild build, BuildListener buildListener);

    @Override
    public IssueStrategyExtensionDescriptor getDescriptor() {
        return (IssueStrategyExtensionDescriptor) super.getDescriptor();
    }
}
