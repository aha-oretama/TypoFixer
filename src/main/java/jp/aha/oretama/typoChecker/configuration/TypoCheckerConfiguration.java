package jp.aha.oretama.typoChecker.configuration;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.Rule;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
@Configuration
public class TypoCheckerConfiguration {

    @Bean
    @RequestScope
    public JLanguageTool jLanguageTool() {
        JLanguageTool jLanguageTool = new JLanguageTool(new AmericanEnglish());
        // Make all rules disable except SpellingCheckRule.
        List<Rule> allActiveRules = jLanguageTool.getAllActiveRules();
        List<String> ruleIds = allActiveRules.stream().filter(rule -> !(rule instanceof SpellingCheckRule)).map(Rule::getId).collect(Collectors.toList());
        jLanguageTool.disableRules(ruleIds);
        return jLanguageTool;
    }
}