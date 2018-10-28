package jp.aha.oretama.typoFixer.model;

import lombok.Data;

/**
 * @author aha-oretama
 */
@Data
public class Comment {
    private String path;
    private Integer position;
    private String body;
    private User user;

    @Data
    public static class User {
        private String login;
    }
}
