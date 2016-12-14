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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jenkinsci.plugins.jiraext.Config;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.scm.ChangeLogSet;

public class RegexWordOfCommitStrategy
    extends IssueStrategyExtension
{
    private static final Logger _logger = Logger.getLogger(RegexWordOfCommitStrategy.class.getName());

    public String regexString;

    @DataBoundConstructor
    public RegexWordOfCommitStrategy(final String regexString)
    {
        super();
        this.regexString = regexString;
    }

    /**
     * Parse a Jira ticket number, ie SSD-101, out of the given ChangeLogSet.Entry.
     *
     * Ticket number is assumed to be the first word of the commit message
     *
     * @param change
     * @param ticketPrefixes
     * @return
     */
    private String getJiraTicket(final ChangeLogSet.Entry change,
                                       final List<String> ticketPrefixes)
    {
        String msg = change.getMsg();
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()){
        	return matcher.group(0);
        }
        return null;
    }

    @Override
    public List<JiraCommit> getJiraCommits(AbstractBuild build,
                                           BuildListener listener)
    {
        List<JiraCommit> jiraCommits = new ArrayList<>();

        try
        {
            _logger.log(Level.FINE, "+iterateTicketsAndApply");
            ChangeLogSet changeSets = build.getChangeSet();
            listener.getLogger().println("ChangeLogSet class: " + changeSets.getClass());

            for (Object entry : changeSets)
            {
                try
                {
                    ChangeLogSet.Entry change = (ChangeLogSet.Entry) entry;
                    _logger.log(Level.FINE, "Found commit: " + (change == null ? "null" : change.getCommitId()));
                    String jiraTicket = getJiraTicket(change, Config.getGlobalConfig().getJiraTickets());
                    if (jiraTicket != null)
                    {
                        _logger.log(Level.FINE, "Ticket discovered: " + jiraTicket);
                            _logger.log(Level.FINE, "Apply to ticket");
                        JiraCommit commit = new JiraCommit(jiraTicket, change);
                        jiraCommits.add(commit);
                    } else
                    {
                        listener.getLogger().println("Unable to find valid Jira prefix in commit message. Valid prefixes are: "
                                + Config.getGlobalConfig().getJiraTickets() + ", the commit message was: " + change.getMsg());
                    }
                }
                catch (Exception e)
                {
                    listener.getLogger().println("ERROR Updating jira notifications");
                    e.printStackTrace(listener.getLogger());
                }
            }
        } catch (Exception e)
        {
            listener.getLogger().println("ERROR Updating jira notifications");
            e.printStackTrace(listener.getLogger());
        }
        return jiraCommits;
    }

    @Extension
    public static class DescriptorImpl
            extends IssueStrategyExtensionDescriptor
    {

        @Override
        public String getDisplayName()
        {
            return "Regex to find commit";
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj != null && obj instanceof RegexWordOfCommitStrategy;
    }
}
