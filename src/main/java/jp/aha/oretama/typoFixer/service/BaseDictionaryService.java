package jp.aha.oretama.typoFixer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aha-oretama
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BaseDictionaryService {

    private static final String JETBRAIN_DICTIONARY = "https://raw.githubusercontent.com/JetBrains/intellij-community/master/spellchecker/src/com/intellij/spellchecker/jetbrains.dic";

    private final RestTemplate restTemplate;

    @Cacheable("dictionary_cache_key")
    public List<String> getDictionaries() {
        RequestEntity requestEntity = RequestEntity.get(URI.create(JETBRAIN_DICTIONARY)).build();
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        String body = responseEntity.getBody();

        List<String> dictionaries = new ArrayList<>();
        try {
            dictionaries.addAll(IOUtils.readLines(new StringReader(body)));
        } catch (IOException e) {
            // If error causes, the error logs out, but application does not stop.
            log.error("JetBrains's dictionary do not read each line.", e);
        }
        return dictionaries;
    }

    @CacheEvict("dictionary_cache_key")
    public void evict() {

    }
}
