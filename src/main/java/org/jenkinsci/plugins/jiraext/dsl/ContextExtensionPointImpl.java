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

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext;
import javaposse.jobdsl.dsl.helpers.step.StepContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import org.jenkinsci.plugins.jiraext.view.JiraExtBuildStep;
import org.jenkinsci.plugins.jiraext.view.JiraExtPublisherStep;

/**
 * @author dalvizu
 */
@Extension(optional=true)
public class ContextExtensionPointImpl
    extends ContextExtensionPoint
    implements Context
{
    @DslExtensionMethod(context = StepContext.class)
    public Object updateJiraExt(Runnable closure) {
        UpdateJiraExtContext context = new UpdateJiraExtContext();
        executeInContext(closure, context);

        JiraExtBuildStep buildStep = new JiraExtBuildStep(context.issueStrategy, context.extensions);
        return buildStep;
    }

    @DslExtensionMethod(context = PublisherContext.class)
    public Object updateJiraExtPublisher(Runnable closure) {
        UpdateJiraExtContext context = new UpdateJiraExtContext();
        executeInContext(closure, context);
        JiraExtPublisherStep publisherStep = new JiraExtPublisherStep(context.issueStrategy, context.extensions);
        return publisherStep;
    }
}
