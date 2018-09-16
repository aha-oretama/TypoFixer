package jp.aha.oretama.typoChecker;

import jp.aha.oretama.typoChecker.model.Diff;
import jp.aha.oretama.typoChecker.model.Suggestion;
import lombok.Data;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author aha-oretama
 */
@Data
@Service
public class TypoCheckerService {

    private final JLanguageTool jLanguageTool;

    private static final Pattern PATH_PATTERN = Pattern.compile("\\+\\+\\+ b/(.+)");
    private static final Pattern LINE_NUMBER_PATTER = Pattern.compile("@@ -[0-9]+,[0-9]+ \\+([0-9]+),[0-9]+ @@");

    public List<Diff> getAdded(String rawDiff) {
        List<String> lines = Arrays.asList(rawDiff.split("\n"));

        List<Diff> diffs = new ArrayList<>();
        int lineNumber = 0;

        for (String line : lines) {
            // Get Path.
            Matcher pathMatcher = PATH_PATTERN.matcher(line);
            if(pathMatcher.find() && pathMatcher.groupCount() >= 1)  {
                Diff diff = new Diff(pathMatcher.group(1), new HashMap<>());
                diffs.add(diff);
                continue;
            }

            // Get start line number.
            Matcher lineNumberMatcher = LINE_NUMBER_PATTER.matcher(line);
            if (lineNumberMatcher.find() && lineNumberMatcher.groupCount() >= 1) {
                // Last diff object needs because line number count up.
                lineNumber = Integer.valueOf(lineNumberMatcher.group(1));
                continue;
            }

            if (!line.startsWith("+") && !line.startsWith(" ")) {
                continue;
            }

            if (line.startsWith("+")){
                // Last diff object needs.
                // Remove first character because first character represents sign.
                diffs.get(diffs.size() - 1).getAdded().put(lineNumber, line.substring(1, line.length()));
            }
            lineNumber++;
        }

        return diffs;
    }

    public List<Suggestion> getSuggestions(List<Diff> added) throws IOException {
        return spellCheck(added);
    }

    public TypoCheckerService setDictionary(List<String> dictionary) {
        for (Rule rule : jLanguageTool.getAllActiveRules()) {
            if(rule instanceof SpellingCheckRule) {
                ((SpellingCheckRule)rule).acceptPhrases(dictionary);
            }
        }
        return this;
    }

    private List<Suggestion> spellCheck(final List<Diff> added) throws IOException {
        List<Suggestion> suggestions = new ArrayList<>();
        for (Diff diff : added) {
            Map<Integer, String> lineAndStr = diff.getAdded();
            for (Integer line : lineAndStr.keySet()) {
                String text = lineAndStr.get(line);
                List<RuleMatch> matches = jLanguageTool.check(text);
                for (RuleMatch match : matches) {
                    suggestions.add(new Suggestion(diff.getPath(), text, line,match));
                }
            }
        }
        return suggestions;
    }
}