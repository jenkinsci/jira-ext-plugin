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
package org.jenkinsci.plugins.jiraext.svc.impl;

import net.rcarz.jiraclient.JiraClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.jenkinsci.plugins.jiraext.Config;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;

/**
 * @author dalvizu
 */
public class JiraClientFactoryImplTest
{
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    @Test
    public void setup()
    {
        Config.getGlobalConfig().setJiraBaseUrl("https://jira.localhost/local");
        Config.getGlobalConfig().setUsername("JenkinsBot");
        Config.getGlobalConfig().setPassword("ChangeMe!");
        Config.getGlobalConfig().setTimeout(20);

        JiraClientFactoryImpl factory = new JiraClientFactoryImpl();
        JiraClient jiraClient = factory.newJiraClient();
        assertEquals("JenkinsBot", jiraClient.getSelf());
        DefaultHttpClient defaultHttpClient = (DefaultHttpClient)jiraClient.getRestClient().getHttpClient();
        assertEquals(20000, HttpConnectionParams.getConnectionTimeout(defaultHttpClient.getParams()));
        assertEquals(20000, HttpConnectionParams.getSoTimeout(defaultHttpClient.getParams()));
    }
}
