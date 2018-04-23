package jp.aha.oretama.typoChecker.model;

import lombok.Data;
import org.languagetool.rules.RuleMatch;

import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
@Data
public class Suggestion {
    private final String path;
    private final Integer line;
    private final RuleMatch match;
    public static final String REGISTER_DICTIONARY = "not typo, register in dictionary and never point out";

    public String createMessage() {
        String typo = match.getSentence().getText().substring(match.getFromPos(), match.getToPos());
        StringBuilder message = new StringBuilder(String.format("Typo? \"%s\" at %d line.", typo, line));

        if (!match.getSuggestedReplacements().isEmpty()) {
            message.append("\n")
                    .append(match.getSuggestedReplacements().stream()
                                    .map(replace -> "- [ ] " + replace)
                                    .collect(Collectors.joining("\n")));
        }
        message.append("\n");
        message.append("- [ ] " + REGISTER_DICTIONARY);
        return message.toString();
    }
}