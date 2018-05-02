package jp.aha.oretama.typoChecker.configuration;

import jp.aha.oretama.typoChecker.language.CodingEnglish;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.Rule;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
@Configuration
public class TypoCheckerConfiguration {

    @Bean
    @RequestScope
    public JLanguageTool jLanguageTool(CacheManager cacheManager) {
        JLanguageTool jLanguageTool = new JLanguageTool(new CodingEnglish());
        // Make all rules disable except SpellingCheckRule.
        List<Rule> allActiveRules = jLanguageTool.getAllActiveRules();
        List<String> ruleIds = allActiveRules.stream().filter(rule -> !(rule instanceof SpellingCheckRule)).map(Rule::getId).collect(Collectors.toList());
        jLanguageTool.disableRules(ruleIds);

        // Set accept phrase
        Cache cache = cacheManager.getCache(CacheConfiguration.CACHE_KEY);
        List<String> dictionary = (List<String>)cache.get(CacheConfiguration.DICTIONARY_CACHE_KEY, List.class);
        for (Rule rule : jLanguageTool.getAllActiveRules()) {
            if (rule instanceof SpellingCheckRule) {
                ((SpellingCheckRule) rule).acceptPhrases(dictionary);
            }
        }
        return jLanguageTool;
    }
}