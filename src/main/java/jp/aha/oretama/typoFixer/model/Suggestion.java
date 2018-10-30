package jp.aha.oretama.typoFixer.model;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
@Data
public class Suggestion {
    private final String path;
    private final String text;
    private final Integer fileLine;
    private final Integer diffLine;
    private final int fromPos;
    private final int toPos;
    private final List<String> suggestedReplacements;
    public static final String REGISTER_DICTIONARY = "Not typo, register in dictionary and never point out.";

    public String createMessage() {
        String typo = text.substring(fromPos, toPos);
        StringBuilder message = new StringBuilder(String.format("Typo? \"%s\" at %d line.", typo, fileLine));

        if (!suggestedReplacements.isEmpty()) {
            message.append("\n")
                    .append(suggestedReplacements.stream()
                            .map(replace -> "- [ ] " + replace)
                            .collect(Collectors.joining("\n")));
        }
        message.append("\n");
        message.append("- [ ] " + REGISTER_DICTIONARY);
        return message.toString();
    }
}