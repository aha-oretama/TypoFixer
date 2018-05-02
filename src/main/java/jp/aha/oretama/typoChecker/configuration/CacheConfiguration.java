package jp.aha.oretama.typoChecker.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @author aha-oretama
 */
@Configuration
public class CacheConfiguration {

    public static final String CACHE_KEY = "TYPO_FIXER_CACHE";
    public static final String DICTIONARY_CACHE_KEY = "DICTIONARY_CACHE";

    @Bean
    CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Collections.singletonList(new ConcurrentMapCache(CACHE_KEY)));
        return cacheManager;
    }
}
