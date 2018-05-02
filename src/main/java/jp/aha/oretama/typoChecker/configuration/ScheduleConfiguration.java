package jp.aha.oretama.typoChecker.configuration;

import jp.aha.oretama.typoChecker.DictionaryRegisterer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author aha-oretama
 */
@Component
@RequiredArgsConstructor
public class ScheduleConfiguration {

    private final DictionaryRegisterer registerer;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000, initialDelay = 30 * 1000)
    public void registerDictionaryTask() throws IOException {
        registerer.registDictionary();
    }
}
