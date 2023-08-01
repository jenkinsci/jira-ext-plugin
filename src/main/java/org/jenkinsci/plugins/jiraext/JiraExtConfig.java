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
import hudson.XmlFile;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Global config
 *
 * @author dalvizu
 */
public class JiraExtConfig
    extends JobProperty<Job<?,?>>
{
    @Override
    public PluginDescriptor getDescriptor()
    {
        return JiraExtConfig.getGlobalConfig();
    }

    public static PluginDescriptor getGlobalConfig()
    {
        return (PluginDescriptor)Jenkins.getInstance().getDescriptor(JiraExtConfig.class);
    }

    @Extension
    public static final class PluginDescriptor
        extends JobPropertyDescriptor
        implements Serializable
    {

        private static final long serialVersionUID = -5366976470724428836L;

        private String jiraBaseUrl;
        private String username;

        private Secret password;

        private String pattern;
        private boolean verboseLogging;
        private Integer timeout;

        public PluginDescriptor()
        {
            super();
            load();
        }

        public Object readResolve()
        {
            if (timeout == null)
            {
                timeout = 10;
            }
            return this;
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

        public Secret getPassword()
        {
            return password;
        }

        public void setPassword(Secret password)
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

        public boolean getVerboseLogging()
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
            setPassword(Secret.fromString(formData.getString("password")));
            setPattern(formData.getString("pattern"));
            setVerboseLogging(formData.getBoolean("verboseLogging"));
            setTimeout(formData.getInt("timeout"));
            save();
            return super.configure(req, formData);
        }

        /**
         * For backwards compatibility
         * This preserves the original file location
         * Additionally allows the old config syntax to be read
         */
        @Override
        protected XmlFile getConfigFile() {
            XmlFile xml = new XmlFile(new File(Jenkins.getInstance().getRootDir(),"org.jenkinsci.plugins.jiraext.Config.xml"));
            xml.getXStream().alias("org.jenkinsci.plugins.jiraext.Config", JiraExtConfig.class);
            xml.getXStream().alias("org.jenkinsci.plugins.jiraext.Config$PluginDescriptor", PluginDescriptor.class);
            return xml;
        }

        public void setTimeout(Integer timeoutInSeconds)
        {
            this.timeout = timeoutInSeconds;
        }

        public Integer getTimeout()
        {
            return timeout;
        }

        public boolean isJiraConfigComplete()
        {
            return StringUtils.isNotEmpty(jiraBaseUrl) && StringUtils.isNotEmpty(username)
                    && StringUtils.isNotEmpty(Secret.toString(password));
        }
    }

}
