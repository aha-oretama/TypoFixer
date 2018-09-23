package jp.aha.oretama.typoFixer.model;

import lombok.Data;
import org.languagetool.rules.RuleMatch;

import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
@Data
public class Suggestion {
    private final String path;
    private final String text;
    private final Integer line;
    private final RuleMatch match;
    public static final String REGISTER_DICTIONARY = "Not typo, register in dictionary and never point out.";

    public String createMessage() {
        String typo = text.substring(match.getFromPos(), match.getToPos());
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