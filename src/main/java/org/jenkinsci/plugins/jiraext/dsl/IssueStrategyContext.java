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
package org.jenkinsci.plugins.jiraext.dsl;

import groovy.lang.Closure;
import hudson.Extension;
import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import org.jenkinsci.plugins.jiraext.view.*;

/**
 * @author dalvizu
 */
@Extension(optional=true)
public class IssueStrategyContext
        extends ContextExtensionPoint
        implements Context
{
    IssueStrategyExtension issueStrategy;

    Closure withXmlClosure;

    public void singleIssue(String issueKey)
    {
        issueStrategy = new SingleTicketStrategy(issueKey);
    }

    public void firstWordOfCommit()
    {
        issueStrategy = new FirstWordOfCommitStrategy();
    }

    public void firstWordOfUpstreamCommit()
    {
        issueStrategy = new FirstWordOfUpstreamCommitStrategy();
    }

    public void mentionedInCommit()
    {
        issueStrategy = new MentionedInCommitStrategy();
    }

    public void mentionedInCommitOrUpstreamCommits()
    {
        issueStrategy = new MentionedInCommitOrUpstreamCommitsStrategy();
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code issueStrategy} node is passed into the configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure withXmlClosure) {
        this.withXmlClosure = withXmlClosure;
    }

}
