package jp.aha.oretama.typoFixer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author aha-oretama
 */
@Data
public class Token {
    private String token;
    @JsonProperty("expires_at")
    private String expiresAt;
}
