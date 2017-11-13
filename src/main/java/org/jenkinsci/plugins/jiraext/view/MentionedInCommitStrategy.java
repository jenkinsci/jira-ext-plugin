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

import org.jenkinsci.plugins.jiraext.Config;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;

import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Find JiraCommits by looking for word in the build's changelog. Issues must match in the pattern defined in
 * global config.
 *
 * <p>For example, if prefix is set to 'JENKINS-,FOO-'</p>
 *
 * <p>And a commit had the message 'Resolved issue [JENKINS-101] fixed stuff', this would return 'JENKINS-101'
 * as a JiraCommit.</p>
 *
 * @author wiedsche
 */
public class MentionedInCommitStrategy
        extends AbstractParsingIssueStrategy
{

    private static final Logger _logger = Logger.getLogger(MentionedInCommitStrategy.class.getName());

    /**
     * Creates a new {@link MentionedInCommitStrategy} object.
     */
    @DataBoundConstructor
    public MentionedInCommitStrategy()
    {
        super();
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj != null) && (obj instanceof MentionedInCommitStrategy);
    }



    /**
     * Parse Jira ticket numbers, ie SSD-101, out of the given ChangeLogSet.Entry.
     *
     * <p>Ticket number should be somewhere in the commit message</p>
     *
     * @param  change
     *
     * @return
     */
    @Override
    public List<JiraCommit> getJiraIssuesFromChangeSet(final ChangeLogSet.Entry change)
    {
        final List<JiraCommit> result = new ArrayList<>();
        final List<String> foundTickets = new ArrayList<>();

        for (String validJiraPrefix : Config.getGlobalConfig().getJiraTickets())
        {
            String msg = change.getMsg();

            while (StringUtils.isNotEmpty(msg))
            {
                final int foundPos = StringUtils.indexOf(msg, validJiraPrefix);

                if (foundPos == -1)
                {
                    break;
                }

                final String firstOccurrence = msg.substring(foundPos + validJiraPrefix.length());
                final String regex = "\\A[0-9]*";
                final Pattern pattern = Pattern.compile(regex);
                final Matcher matcher = pattern.matcher(firstOccurrence);

                if (!matcher.find())
                {
                    break;
                }
                /**
                 * It is totally unclear why a regex for the entire
                 * Jira ticket identifier is not a configuration item
                 * It is strange to be seperating the ticket number from the
                 * prefix, just to put it back together again -
                 * without using either seperately.
                 *
                 * This would be a trivial change to the config, and
                 * #probably# not break any existing code other than these isolated
                 * areas where the prefix is being searched.
                 */

                final String ticketNumber = matcher.group();

                if (StringUtils.isEmpty(ticketNumber))
                {
                    break;
                }

                final String resultingTicket = validJiraPrefix + ticketNumber;

                if (!foundTickets.contains(resultingTicket))
                {
                    foundTickets.add(resultingTicket);
                    result.add(new JiraCommit(resultingTicket, change));
                }

                msg = firstOccurrence;
            }
        }

        return result;
    }

    @Extension
    public static class DescriptorImpl
            extends IssueStrategyExtensionDescriptor
    {

        @Override
        public String getDisplayName()
        {
            return "Mentioned somewhere in commit message";
        }
    }
}
