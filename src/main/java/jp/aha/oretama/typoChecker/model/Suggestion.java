package jp.aha.oretama.typoChecker.model;

import lombok.Data;
import org.languagetool.rules.RuleMatch;

/**
 * @author aha-oretama
 */
@Data
public class Suggestion {
    private final String path;
    private final Integer line;
    private final RuleMatch match;
}