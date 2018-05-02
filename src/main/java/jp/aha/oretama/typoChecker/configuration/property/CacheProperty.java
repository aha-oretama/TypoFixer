package jp.aha.oretama.typoChecker.configuration.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author aha-oretama
 */
@ConfigurationProperties(
        prefix = "application.cache"
)
@Component
@Data
public class CacheProperty {
    private String name;
    private String key;
}
