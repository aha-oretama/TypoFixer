package jp.aha.oretama.typoChecker.configuration;

import jp.aha.oretama.typoChecker.service.BaseDictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author aha-oretama
 */
@Component
@RequiredArgsConstructor
public class ScheduleConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    private final BaseDictionaryService dictionaryService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateDictionaryCache() {
        dictionaryService.evict();
        dictionaryService.getDictionaries();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        dictionaryService.getDictionaries();
    }
}
