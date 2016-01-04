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

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Global config
 *
 * @author dalvizu
 */
public class Config
    extends JobProperty<Job<?,?>>
{
    @Override
    public PluginDescriptor getDescriptor()
    {
        return Config.getGlobalConfig();
    }

    public static PluginDescriptor getGlobalConfig()
    {
        return (PluginDescriptor)Jenkins.getInstance().getDescriptor(Config.class);
    }

    @Extension
    public static final class PluginDescriptor
        extends JobPropertyDescriptor
        implements Serializable
    {

        private static final long serialVersionUID = -5366976470724428836L;

        private String jiraBaseUrl;
        private String username;
        private String password;
        private String pattern;
        private boolean verboseLogging;

        public PluginDescriptor()
        {
            super();
            load();
        }

        @Override
        public String getDisplayName()
        {
            return "Jira-ext Config";
        }

        public String getJiraBaseUrl()
        {
            return jiraBaseUrl;
        }

        public void setJiraBaseUrl(String jiraBaseUrl)
        {
            this.jiraBaseUrl = jiraBaseUrl;
        }

        public String getUsername()
        {
            return username;
        }

        public void setUsername(String username)
        {
            this.username = username;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }

        public String getPattern()
        {
            return pattern;
        }

        public void setPattern(String pattern)
        {
            this.pattern = pattern;
        }

        public void setVerboseLogging(boolean verboseLogging)
        {
            this.verboseLogging = verboseLogging;
        }

        public boolean isVerboseLogging()
        {
            return verboseLogging;
        }

        public List<String> getJiraTickets()
        {
            if (StringUtils.isBlank(pattern))
            {
                return new ArrayList<>();
            }
            return Arrays.asList(pattern.split(","));
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException
        {
            setJiraBaseUrl(formData.getString("jiraBaseUrl"));
            setUsername(formData.getString("username"));
            setPassword(formData.getString("password"));
            setPattern(formData.getString("pattern"));
            setVerboseLogging(formData.getBoolean("verboseLogging"));
            return true;
        }
    }

}
