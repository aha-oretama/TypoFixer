package jp.aha.oretama.typoFixer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author aha-oretama
 */

@Data
public class Event {
    private String action;

    // For pull request event.
    private int number;
    @JsonProperty("pull_request")
    private PullRequest pullRequest;
    private Installation installation;

    // For comment edited.
    private Change changes;
    private Comment comment;

    @Data
    public static class PullRequest {
        private String url;
        @JsonProperty("diff_url")
        private String diffUrl;
        @JsonProperty("review_comments_url")
        private String reviewCommentsUrl;
        private Base base;
        private Head head;
    }

    // Branch to which pull request merges.
    @Data
    public static class Base {
        private Repo repo;
    }

    // PullRequest's branch.
    @Data
    public static class Head {
        private String sha;
        private Repo repo;
        private String ref;
    }

    @Data
    public static class Repo {
        @JsonProperty("git_url")
        private String gitUrl;
        @JsonProperty("contents_url")
        private String contentsUrl;
        private String name;
        private Owner owner;
    }

    @Data
    public static class Owner {
        private String login;
    }

    @Data
    public static class Installation {
        private String id;
    }

    @Data
    public static class Change {
        private Body body;
    }

    @Data
    public static class Body {
        private String from;
    }

    @Data
    public static class Comment {
        private String body;
        private String path;
    }
}
