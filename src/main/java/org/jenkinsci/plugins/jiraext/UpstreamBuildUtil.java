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
package org.jenkinsci.plugins.jiraext;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Run;
import jenkins.model.Jenkins;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dalvizu
 */
public class UpstreamBuildUtil
{
    private static Logger logger = Logger.getLogger(UpstreamBuildUtil.class.getName());

    /**
     * Get the root upstream build which caused this build. Useful in build pipelines to
     * be able to extract the changes which started the pipeline in later stages of
     * the pipeline
     *
     * @return
     */
    public static AbstractBuild getUpstreamBuild(AbstractBuild<?, ?> build)
    {
        logger.log(Level.FINE, "Find build upstream of " + build.getId());

        Cause.UpstreamCause cause = getUpstreamCause(build);
        if (cause == null)
        {
            logger.log(Level.FINE, "No upstream cause, so must be upstream build: " + build.getId());
            return build;
        }
        logger.log(Level.FINE, "Found upstream cause: " + cause.toString() + "(" + cause.getShortDescription() + ")");
        AbstractProject project = (AbstractProject) Jenkins.get().getItem(cause.getUpstreamProject(), build.getProject());
        if (project == null)
        {
            logger.log(Level.WARNING, "Found an UpstreamCause (" + cause.toString()
                    + "), but the upstream project (" + cause.getUpstreamProject() + ") does not appear to be valid!");
            logger.log(Level.WARNING, "Using build [" + build.getId() + "] as the upstream build - this is likely incorrect.");

            return build;
        }
        AbstractBuild upstreamBuild = project.getBuildByNumber(cause.getUpstreamBuild());
        if (upstreamBuild == null)
        {
            logger.log(Level.WARNING, "Found an UpstreamCause (" + cause.toString()
                    + "), and an upstream project (" + project.getName() + "), but the build is invalid!" + cause.getUpstreamBuild());
            logger.log(Level.WARNING, "Using build [" + build.getId() + "] as the upstream build - this is likely incorrect.");
            return build;
        }
        return getUpstreamBuild(upstreamBuild);
    }

    private static Cause.UpstreamCause getUpstreamCause(Run run)
    {
        if (run == null)
        {
            return null;
        }
        List<Cause> causes = run.getCauses();
        for (Cause cause : causes)
        {
            if (cause instanceof Cause.UpstreamCause)
            {
                return (Cause.UpstreamCause)cause;
            }
        }
        return null;
    }

    public static boolean hasUpstreamBuild(Run run)
    {
        return getUpstreamCause(run) != null;
    }
}
