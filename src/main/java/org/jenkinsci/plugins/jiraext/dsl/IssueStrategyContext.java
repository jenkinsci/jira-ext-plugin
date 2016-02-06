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
import groovy.lang.MetaClass;
import groovy.util.Node;
import hudson.Extension;
import javaposse.jobdsl.dsl.Item;
import javaposse.jobdsl.dsl.JobManagement;
import javaposse.jobdsl.dsl.helpers.AbstractExtensibleContext;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.jenkinsci.plugins.jiraext.view.FirstWordOfCommitStrategy;
import org.jenkinsci.plugins.jiraext.view.FirstWordOfUpstreamCommitStrategy;
import org.jenkinsci.plugins.jiraext.view.IssueStrategyExtension;
import org.jenkinsci.plugins.jiraext.view.MentionedInCommitStrategy;
import org.jenkinsci.plugins.jiraext.view.SingleTicketStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dalvizu
 */
@Extension(optional=true)
public class IssueStrategyContext
        extends AbstractExtensibleContext
{
    // never persist the MetaClass
    private transient MetaClass metaClass;

    public IssueStrategyContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item);
         this.metaClass = InvokerHelper.getMetaClass(this.getClass());
    }

    IssueStrategyExtension issueStrategy;

    Closure withXmlClosure;

    List<Node> issueStrategyNodes = new ArrayList<>();

    @Override
    protected void addExtensionNode(Node node)
    {
        issueStrategyNodes.add(node);
    }

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

    /**
     * Allows direct manipulation of the generated XML. The {@code issueStrategy} node is passed into the configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure withXmlClosure) {
        this.withXmlClosure = withXmlClosure;
    }

    public Object getProperty(String property) {
        return getMetaClass().getProperty(this, property);
    }

    public void setProperty(String property, Object newValue) {
        getMetaClass().setProperty(this, property, newValue);
    }

    public Object invokeMethod(String name, Object args) {
        return getMetaClass().invokeMethod(this, name, args);
    }

    public MetaClass getMetaClass() {
        if (metaClass == null) {
            metaClass = InvokerHelper.getMetaClass(getClass());
        }
        return metaClass;
    }

    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }
}
