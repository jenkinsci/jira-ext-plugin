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
import hudson.scm.ChangeLogSet;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.jiraext.JiraExtConfig;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find JiraCommits by looking for the first word in the build's
 * changelog. Issues must match in the pattern defined in global config.
 *
 * For example, if prefix is set to 'JENKINS-,FOO-'
 *
 * And a commit had the message 'JENKINS-101 fixed stuff', this would return
 * 'JENKINS-101' as a JiraCommit.
 *
 * @author dalvizu
 */
public class FirstWordOfCommitStrategy
    extends AbstractParsingIssueStrategy
{
    private static final Logger _logger = Logger.getLogger(FirstWordOfCommitStrategy.class.getName());

    @DataBoundConstructor
    public FirstWordOfCommitStrategy()
    {
        super();
    }

    /**
     * Parse a Jira ticket number, ie SSD-101, out of the given ChangeLogSet.Entry.
     *
     * Ticket number is assumed to be the first word of the commit message
     *
     * @param change - the change entry to
     * @return
     */
    @Override
    public List<JiraCommit> getJiraIssuesFromChangeSet(final ChangeLogSet.Entry change)
    {
        String msg = change.getMsg();
        String firstWordOfTicket;
        firstWordOfTicket = msg.substring(0, (msg.contains(" ") ? StringUtils.indexOf(msg, " ") : msg.length()));

        final String regex = "([A-Z][0-9A-Z_]+-)([0-9]+)";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(firstWordOfTicket);

        if (!matcher.find())
        {
            return null;
        }

        List<String> jiraPrefixes = JiraExtConfig.getGlobalConfig().getJiraTickets();

        if (jiraPrefixes.contains(matcher.group(1)))
        {
            return Arrays.asList(new JiraCommit(matcher.group(0), change));
        }

        return null;
    }

    @Extension
    public static class DescriptorImpl
            extends IssueStrategyExtensionDescriptor
    {

        @Override
        public String getDisplayName()
        {
            return "First word of commit";
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj != null && obj instanceof FirstWordOfCommitStrategy;
    }

    @Override
    public int hashCode()
    {
        return FirstWordOfCommitStrategy.class.hashCode();
    }
}
