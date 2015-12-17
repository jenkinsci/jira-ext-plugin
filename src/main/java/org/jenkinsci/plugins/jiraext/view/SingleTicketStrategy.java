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
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Return a Single issue. Probably not particularly useful for anything outside
 * of testing integration.
 *
 * @author dalvizu
 */
public class SingleTicketStrategy
    extends IssueStrategyExtension
{
    private static final Logger _logger = Logger.getLogger(SingleTicketStrategy.class.getName());

    private String issueKey;

    @DataBoundConstructor
    public SingleTicketStrategy(String issueKey)
    {
        super();
        this.issueKey = issueKey;
    }

    @Override
    public List<JiraCommit> getJiraCommits(AbstractBuild build,
                                           BuildListener listener)
    {
        List<JiraCommit> jiraCommits = new ArrayList<>();
        jiraCommits.add(new JiraCommit(issueKey, null));
        return jiraCommits;
    }

    @Extension
    public static class DescriptorImpl
            extends IssueStrategyExtensionDescriptor
    {

        @Override
        public String getDisplayName()
        {
            return "Manually specifying a single issue";
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj != null && obj instanceof SingleTicketStrategy;
    }
}
