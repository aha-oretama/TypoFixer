package jp.aha.oretama.typoChecker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.aha.oretama.typoChecker.model.Config;
import jp.aha.oretama.typoChecker.model.Diff;
import jp.aha.oretama.typoChecker.service.filter.ExtensionFilter;
import jp.aha.oretama.typoChecker.service.filter.Filter;
import jp.aha.oretama.typoChecker.service.filter.ParseFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author aha-oretama
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilterService {

    private final ObjectMapper mapper;

    private LinkedList<Filter> filters = new LinkedList<>();

    public FilterService setParse() {
        filters.offer(new ParseFilter());
        return this;
    }

    public FilterService setConfig(String content) {
        Config config = null;
        try {
            config = mapper.readValue(content, Config.class);
        } catch (IOException e) {
            log.warn("typo-fixer.json could not be deserialized.", e);
        }
        if (config != null && !config.getExtensions().isEmpty()) {
            filters.offer(new ExtensionFilter(config.getExtensions()));
        }
        return this;
    }

    public void filtering(List<Diff> added) {
        for (int i = 0; i < filters.size(); i++) {
            filters.remove().filtering(added);
        }
    }
}

