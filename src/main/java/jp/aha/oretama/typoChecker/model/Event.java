package jp.aha.oretama.typoChecker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author aha-oretama
 */

// {
//  "action": "created",
//  "issue": {
//    "url": "https://api.github.com/repos/aha-oretama/test/issues/1",
//    "repository_url": "https://api.github.com/repos/aha-oretama/test",
//    "labels_url": "https://api.github.com/repos/aha-oretama/test/issues/1/labels{/name}",
//    "comments_url": "https://api.github.com/repos/aha-oretama/test/issues/1/comments",
//    "events_url": "https://api.github.com/repos/aha-oretama/test/issues/1/events",
//    "html_url": "https://github.com/aha-oretama/test/pull/1",
//    "id": 243209919,
//    "number": 1,
//    "title": "create TestFile",
//    "user": {
//      "login": "aha-oretama",
//      "id": 7259161,
//      "avatar_url": "https://avatars0.githubusercontent.com/u/7259161?v=4",
//      "gravatar_id": "",
//      "url": "https://api.github.com/users/aha-oretama",
//      "html_url": "https://github.com/aha-oretama",
//      "followers_url": "https://api.github.com/users/aha-oretama/followers",
//      "following_url": "https://api.github.com/users/aha-oretama/following{/other_user}",
//      "gists_url": "https://api.github.com/users/aha-oretama/gists{/gist_id}",
//      "starred_url": "https://api.github.com/users/aha-oretama/starred{/owner}{/repo}",
//      "subscriptions_url": "https://api.github.com/users/aha-oretama/subscriptions",
//      "organizations_url": "https://api.github.com/users/aha-oretama/orgs",
//      "repos_url": "https://api.github.com/users/aha-oretama/repos",
//      "events_url": "https://api.github.com/users/aha-oretama/events{/privacy}",
//      "received_events_url": "https://api.github.com/users/aha-oretama/received_events",
//      "type": "User",
//      "site_admin": false
//    },
//    "labels": [
//
//    ],
//    "state": "open",
//    "locked": false,
//    "assignee": null,
//    "assignees": [
//
//    ],
//    "milestone": null,
//    "comments": 5,
//    "created_at": "2017-07-16T01:02:05Z",
//    "updated_at": "2018-03-24T06:49:21Z",
//    "closed_at": null,
//    "author_association": "OWNER",
//    "pull_request": {
//      "url": "https://api.github.com/repos/aha-oretama/test/pulls/1",
//      "html_url": "https://github.com/aha-oretama/test/pull/1",
//      "diff_url": "https://github.com/aha-oretama/test/pull/1.diff",
//      "patch_url": "https://github.com/aha-oretama/test/pull/1.patch"
//    },
//    "body": ""
//  },
//  "comment": {
//    "url": "https://api.github.com/repos/aha-oretama/test/issues/comments/375851434",
//    "html_url": "https://github.com/aha-oretama/test/pull/1#issuecomment-375851434",
//    "issue_url": "https://api.github.com/repos/aha-oretama/test/issues/1",
//    "id": 375851434,
//    "user": {
//      "login": "aha-oretama",
//      "id": 7259161,
//      "avatar_url": "https://avatars0.githubusercontent.com/u/7259161?v=4",
//      "gravatar_id": "",
//      "url": "https://api.github.com/users/aha-oretama",
//      "html_url": "https://github.com/aha-oretama",
//      "followers_url": "https://api.github.com/users/aha-oretama/followers",
//      "following_url": "https://api.github.com/users/aha-oretama/following{/other_user}",
//      "gists_url": "https://api.github.com/users/aha-oretama/gists{/gist_id}",
//      "starred_url": "https://api.github.com/users/aha-oretama/starred{/owner}{/repo}",
//      "subscriptions_url": "https://api.github.com/users/aha-oretama/subscriptions",
//      "organizations_url": "https://api.github.com/users/aha-oretama/orgs",
//      "repos_url": "https://api.github.com/users/aha-oretama/repos",
//      "events_url": "https://api.github.com/users/aha-oretama/events{/privacy}",
//      "received_events_url": "https://api.github.com/users/aha-oretama/received_events",
//      "type": "User",
//      "site_admin": false
//    },
//    "created_at": "2018-03-24T06:49:21Z",
//    "updated_at": "2018-03-24T06:49:21Z",
//    "author_association": "OWNER",
//    "body": "hello"
//  },
//  "repository": {
//    "id": 97314451,
//    "name": "test",
//    "full_name": "aha-oretama/test",
//    "owner": {
//      "login": "aha-oretama",
//      "id": 7259161,
//      "avatar_url": "https://avatars0.githubusercontent.com/u/7259161?v=4",
//      "gravatar_id": "",
//      "url": "https://api.github.com/users/aha-oretama",
//      "html_url": "https://github.com/aha-oretama",
//      "followers_url": "https://api.github.com/users/aha-oretama/followers",
//      "following_url": "https://api.github.com/users/aha-oretama/following{/other_user}",
//      "gists_url": "https://api.github.com/users/aha-oretama/gists{/gist_id}",
//      "starred_url": "https://api.github.com/users/aha-oretama/starred{/owner}{/repo}",
//      "subscriptions_url": "https://api.github.com/users/aha-oretama/subscriptions",
//      "organizations_url": "https://api.github.com/users/aha-oretama/orgs",
//      "repos_url": "https://api.github.com/users/aha-oretama/repos",
//      "events_url": "https://api.github.com/users/aha-oretama/events{/privacy}",
//      "received_events_url": "https://api.github.com/users/aha-oretama/received_events",
//      "type": "User",
//      "site_admin": false
//    },
//    "private": false,
//    "html_url": "https://github.com/aha-oretama/test",
//    "description": null,
//    "fork": false,
//    "url": "https://api.github.com/repos/aha-oretama/test",
//    "forks_url": "https://api.github.com/repos/aha-oretama/test/forks",
//    "keys_url": "https://api.github.com/repos/aha-oretama/test/keys{/key_id}",
//    "collaborators_url": "https://api.github.com/repos/aha-oretama/test/collaborators{/collaborator}",
//    "teams_url": "https://api.github.com/repos/aha-oretama/test/teams",
//    "hooks_url": "https://api.github.com/repos/aha-oretama/test/hooks",
//    "issue_events_url": "https://api.github.com/repos/aha-oretama/test/issues/events{/number}",
//    "events_url": "https://api.github.com/repos/aha-oretama/test/events",
//    "assignees_url": "https://api.github.com/repos/aha-oretama/test/assignees{/user}",
//    "branches_url": "https://api.github.com/repos/aha-oretama/test/branches{/branch}",
//    "tags_url": "https://api.github.com/repos/aha-oretama/test/tags",
//    "blobs_url": "https://api.github.com/repos/aha-oretama/test/git/blobs{/sha}",
//    "git_tags_url": "https://api.github.com/repos/aha-oretama/test/git/tags{/sha}",
//    "git_refs_url": "https://api.github.com/repos/aha-oretama/test/git/refs{/sha}",
//    "trees_url": "https://api.github.com/repos/aha-oretama/test/git/trees{/sha}",
//    "statuses_url": "https://api.github.com/repos/aha-oretama/test/statuses/{sha}",
//    "languages_url": "https://api.github.com/repos/aha-oretama/test/languages",
//    "stargazers_url": "https://api.github.com/repos/aha-oretama/test/stargazers",
//    "contributors_url": "https://api.github.com/repos/aha-oretama/test/contributors",
//    "subscribers_url": "https://api.github.com/repos/aha-oretama/test/subscribers",
//    "subscription_url": "https://api.github.com/repos/aha-oretama/test/subscription",
//    "commits_url": "https://api.github.com/repos/aha-oretama/test/commits{/sha}",
//    "git_commits_url": "https://api.github.com/repos/aha-oretama/test/git/commits{/sha}",
//    "comments_url": "https://api.github.com/repos/aha-oretama/test/comments{/number}",
//    "issue_comment_url": "https://api.github.com/repos/aha-oretama/test/issues/comments{/number}",
//    "contents_url": "https://api.github.com/repos/aha-oretama/test/contents/{+path}",
//    "compare_url": "https://api.github.com/repos/aha-oretama/test/compare/{base}...{head}",
//    "merges_url": "https://api.github.com/repos/aha-oretama/test/merges",
//    "archive_url": "https://api.github.com/repos/aha-oretama/test/{archive_format}{/ref}",
//    "downloads_url": "https://api.github.com/repos/aha-oretama/test/downloads",
//    "issues_url": "https://api.github.com/repos/aha-oretama/test/issues{/number}",
//    "pulls_url": "https://api.github.com/repos/aha-oretama/test/pulls{/number}",
//    "milestones_url": "https://api.github.com/repos/aha-oretama/test/milestones{/number}",
//    "notifications_url": "https://api.github.com/repos/aha-oretama/test/notifications{?since,all,participating}",
//    "labels_url": "https://api.github.com/repos/aha-oretama/test/labels{/name}",
//    "releases_url": "https://api.github.com/repos/aha-oretama/test/releases{/id}",
//    "deployments_url": "https://api.github.com/repos/aha-oretama/test/deployments",
//    "created_at": "2017-07-15T12:27:04Z",
//    "updated_at": "2017-07-15T12:27:04Z",
//    "pushed_at": "2017-07-16T01:02:05Z",
//    "git_url": "git://github.com/aha-oretama/test.git",
//    "ssh_url": "git@github.com:aha-oretama/test.git",
//    "clone_url": "https://github.com/aha-oretama/test.git",
//    "svn_url": "https://github.com/aha-oretama/test",
//    "homepage": null,
//    "size": 0,
//    "stargazers_count": 0,
//    "watchers_count": 0,
//    "language": null,
//    "has_issues": true,
//    "has_projects": true,
//    "has_downloads": true,
//    "has_wiki": true,
//    "has_pages": false,
//    "forks_count": 0,
//    "mirror_url": null,
//    "archived": false,
//    "open_issues_count": 1,
//    "license": null,
//    "forks": 0,
//    "open_issues": 1,
//    "watchers": 0,
//    "default_branch": "master"
//  },
//  "sender": {
//    "login": "aha-oretama",
//    "id": 7259161,
//    "avatar_url": "https://avatars0.githubusercontent.com/u/7259161?v=4",
//    "gravatar_id": "",
//    "url": "https://api.github.com/users/aha-oretama",
//    "html_url": "https://github.com/aha-oretama",
//    "followers_url": "https://api.github.com/users/aha-oretama/followers",
//    "following_url": "https://api.github.com/users/aha-oretama/following{/other_user}",
//    "gists_url": "https://api.github.com/users/aha-oretama/gists{/gist_id}",
//    "starred_url": "https://api.github.com/users/aha-oretama/starred{/owner}{/repo}",
//    "subscriptions_url": "https://api.github.com/users/aha-oretama/subscriptions",
//    "organizations_url": "https://api.github.com/users/aha-oretama/orgs",
//    "repos_url": "https://api.github.com/users/aha-oretama/repos",
//    "events_url": "https://api.github.com/users/aha-oretama/events{/privacy}",
//    "received_events_url": "https://api.github.com/users/aha-oretama/received_events",
//    "type": "User",
//    "site_admin": false
//  },
//  "installation": {
//    "id": 99470
//  }
//}
@Data
public class Event {
    private String action;
    private Comment comment;
    private Issue issue;
    private Installation installation;

    @Data
    public static class Comment {
        private String id;
        private String body;
    }

    @Data
    public static class Issue {
        private String number;
        @JsonProperty("comments_url")
        private String commentsUrl;
    }

    @Data
    public static class Installation {
        private String id;
    }
}
