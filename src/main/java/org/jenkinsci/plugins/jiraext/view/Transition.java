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
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.kohsuke.stapler.DataBoundConstructor;
import java.util.logging.Logger;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transition a JIRA issue
 *
 * @author dalvizu
 */
public class Transition
    extends JiraOperationExtension
{
    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    public String transitionName;

    @DataBoundConstructor
    public Transition(String transitionName)
    {
        this.transitionName = transitionName;
    }

    @Override
    public void perform(List<JiraCommit> jiraCommitList,
                        AbstractBuild build, Launcher launcher, BuildListener listener)
    {
        try {
            for (JiraCommit jiraCommit : JiraCommit.filterDuplicateIssues(jiraCommitList)) {
                listener.getLogger().println("Transition a ticket: " + jiraCommit.getJiraTicket());

                //Make a copy of all transitions since we will be removing the ones that are done
                List<String> transitions = Arrays.stream(StringUtils.split(transitionName, ","))
                        .map(t -> t.trim())
                        .collect(Collectors.toList());
                boolean didAnyTransition = false;

                //Loop through all transitions until one works
                while (transitions.size() > 0)
                {
                    Iterator<String> transitionIter = transitions.iterator();
                    boolean didTransition = false;
                    while (transitionIter.hasNext())
                    {
                        String transition = transitionIter.next();
                        try
                        {
                            getJiraClientSvc().changeWorkflowOfTicket(jiraCommit.getJiraTicket(), transition);

                            //Transition worked. Remove it from the list, and try the next one
                            listener.getLogger().println("Performed transition: " + transition);
                            transitionIter.remove();
                            didAnyTransition = true;
                            didTransition = true;
                        } catch (Throwable t)
                        {
                            //Ignore and try next transition
                            logger.fine("JIRA transition " + transition + " failed on ticket " + jiraCommit.getJiraTicket());
                        }
                    }
                    if (!didTransition)
                    {
                        //Did not perform any transitions this round. We are done!
                        transitions.clear();
                    }
                }

                if (!didAnyTransition)
                {
                    //No transitions were done. Show error message
                    listener.getLogger().println("ERROR Updating JIRA with transitions [" + transitionName + "]. No transitions were valid. Continuing");
                }

            }
        } catch (Throwable t)
        {
            listener.getLogger().println("ERROR Updating JIRA");
            t.printStackTrace(listener.getLogger());
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof Transition))
        {
            return false;
        }
        Transition other = (Transition)obj;
        return StringUtils.equals(transitionName, other.transitionName);
    }

    @Override
    public String toString()
    {
        return "Transition[transitionName=" + transitionName +"]";
    }
    @Extension
    public static class DescriptorImpl
        extends JiraOperationExtensionDescriptor {

        @Override
        public String getDisplayName()
        {
            return "Transition tickets";
        }
    }
}
