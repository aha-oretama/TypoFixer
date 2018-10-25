package jp.aha.oretama.typoFixer.model;

import lombok.Getter;

/**
 * @author aha-oretama
 */
public enum Status {
    Error("error"),
    Failure("failure"),
    Pending("pending"),
    Success("success");

    @Getter
    private final String status;

    Status(String status) {
        this.status = status;
    }
}
