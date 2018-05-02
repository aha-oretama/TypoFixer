package jp.aha.oretama.typoChecker;

import jp.aha.oretama.typoChecker.configuration.CacheConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.List;

/**
 * @author aha-oretama
 */
@Service
@RequiredArgsConstructor
public class DictionaryRegisterer {

    private static final String JETBRAIN_DICTIONARY = "https://raw.githubusercontent.com/JetBrains/intellij-community/master/spellchecker/src/com/intellij/spellchecker/jetbrains.dic";

    private final RestTemplate restTemplate;
    private final CacheManager cacheManager;

    public synchronized void registDictionary() throws IOException {

        RequestEntity requestEntity = RequestEntity.get(URI.create(JETBRAIN_DICTIONARY)).build();
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        String body = responseEntity.getBody();

        List<String> dictionaries = IOUtils.readLines(new StringReader(body));
        Cache cache = cacheManager.getCache(CacheConfiguration.CACHE_KEY);
        cache.evict(CacheConfiguration.DICTIONARY_CACHE_KEY);
        cache.put(CacheConfiguration.DICTIONARY_CACHE_KEY, dictionaries);
    }
}
