package jp.aha.oretama.typoChecker.configuration;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author aha-oretama
 */
@Configuration
public class TypoCheckerConfiguration {
    @Bean
    public JLanguageTool jLanguageTool() {
        return new JLanguageTool(new AmericanEnglish());
    }
}