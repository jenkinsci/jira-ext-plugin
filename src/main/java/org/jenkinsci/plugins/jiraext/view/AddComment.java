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

import com.google.common.base.Optional;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.plugins.git.GitChangeSet;
import hudson.scm.ChangeLogSet;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.jiraext.domain.JiraCommit;
import org.jenkinsci.plugins.jiraext.svc.JiraClientSvc;
import org.kohsuke.stapler.DataBoundConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Add a comment to a JIRA issue
 *
 * @author dalvizu
 */
public class AddComment
    extends JiraOperationExtension
{

    public final boolean postCommentForEveryCommit;

    public final String commentText;

    @DataBoundConstructor
    public AddComment(boolean postCommentForEveryCommit, String commentText)
    {
        this.postCommentForEveryCommit = postCommentForEveryCommit;
        this.commentText = commentText;
    }

    @Override
    public void perform(List<JiraCommit> jiraCommitList,
                        AbstractBuild build, Launcher launcher, BuildListener listener)
    {

        for (JiraCommit jiraCommit : filterJiraCommitList(jiraCommitList))
        {
            try
            {
                listener.getLogger().println("Updating ticket: " + jiraCommit.getJiraTicket());
                String comment;
                if (jiraCommit.getChangeSet().isPresent())
                {
                    ChangeLogSet.Entry entry = jiraCommit.getChangeSet().get();
                    listener.getLogger().println("\tchange.msg()\t" + entry.getMsgAnnotated());
                    listener.getLogger().println("\tchange.getCommitId()\t" + entry.getCommitId());
                    listener.getLogger().println("\tchange.getAuthor()\t" + entry.getAuthor());
                }
                getJiraClientSvc().addCommentToTicket(jiraCommit.getJiraTicket(),
                        buildComment(jiraCommit.getChangeSet(), build.getEnvironment(listener)));
            }
            catch (Throwable t)
            {
                listener.getLogger().println("ERROR Updating jira notifications");
                t.printStackTrace(listener.getLogger());
            }
        }
    }

    private List<JiraCommit> filterJiraCommitList(List<JiraCommit> jiraCommitList)
    {
        if (postCommentForEveryCommit)
        {
            return jiraCommitList;
        }
        else
        {
            return JiraCommit.filterDuplicateIssues(jiraCommitList);
        }
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private String buildComment(Optional<ChangeLogSet.Entry> change,
                                EnvVars environment)
    {
        String finalComment = commentText;
        finalComment = environment.expand(finalComment);

        if (!change.isPresent())
        {
            return finalComment;
        }

        String message = "";
        ChangeLogSet.Entry entry = change.get();
        if (entry instanceof GitChangeSet)
        {
            message = ((GitChangeSet) entry).getComment();
        }
        else
        {
            message = entry.getMsg();
        }

        finalComment = StringUtils.replace(finalComment, "$AUTHOR", (entry.getAuthor() == null ? "null" : entry.getAuthor().getDisplayName()));
        finalComment = StringUtils.replace(finalComment, "$COMMIT_ID", entry.getCommitId());
        finalComment = StringUtils.replace(finalComment, "$COMMIT_DATE",
                DATE_FORMAT.format(new Date(entry.getTimestamp())));
        finalComment = StringUtils.replace(finalComment, "$COMMIT_MESSAGE", message);
        return finalComment;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        AddComment other = (AddComment)obj;
        return postCommentForEveryCommit == other.postCommentForEveryCommit
                && Objects.equals(commentText, other.commentText);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(postCommentForEveryCommit, commentText);
    }

    @Override
    public String toString()
    {
        return "AddComment[postCommentForEveryCommit=" + postCommentForEveryCommit +
                ",commentText=" + commentText + "]";
    }

    @Extension
    public static class DescriptorImpl
        extends JiraOperationExtensionDescriptor {

        @Override
        public String getDisplayName()
        {
            return "Add a comment";
        }
    }
}
