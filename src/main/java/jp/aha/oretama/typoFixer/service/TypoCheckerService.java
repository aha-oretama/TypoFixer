package jp.aha.oretama.typoFixer.service;

import jp.aha.oretama.typoFixer.model.Diff;
import jp.aha.oretama.typoFixer.model.Event;
import jp.aha.oretama.typoFixer.model.Suggestion;
import jp.aha.oretama.typoFixer.model.Token;
import jp.aha.oretama.typoFixer.repository.GitHubRepository;
import jp.aha.oretama.typoFixer.util.extenstion.FuntionalExtension;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author aha-oretama
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class TypoCheckerService {

    private final JLanguageTool jLanguageTool;
    private final GitHubRepository repository;

    private static final Pattern PATH_PATTERN = Pattern.compile("^\\+\\+\\+ b/(.+)$");
    private static final Pattern LINE_NUMBER_PATTER = Pattern.compile("^@@ -[0-9]+,?[0-9]* \\+([0-9]+),?[0-9]* @@");

    public List<Diff> getAdded(String rawDiff) {
        List<String> lines = Arrays.asList(rawDiff.split("\n"));

        List<Diff> diffs = new ArrayList<>();
        // fileLine is committed file's line number.
        int fileLine = 0;
        // diffLine is line number which GitHub displays in diff page and include all of +, - , not change lines.
        int diffLine = 0;
        // diffLine is increasing in the file. But diffLine is reset when the file is changed.
        boolean resetDiffLine = true;

        for (String line : lines) {
            // Get Path.
            Matcher pathMatcher = PATH_PATTERN.matcher(line);
            if (pathMatcher.find() && pathMatcher.groupCount() >= 1) {
                Diff diff = new Diff(pathMatcher.group(1), new ArrayList<>());
                diffs.add(diff);
                resetDiffLine = true;
                continue;
            }

            // Get start line number. Initialize line number.
            Matcher lineNumberMatcher = LINE_NUMBER_PATTER.matcher(line);
            if (lineNumberMatcher.find() && lineNumberMatcher.groupCount() >= 1) {
                // Last diff object needs because line number count up.
                fileLine = Integer.valueOf(lineNumberMatcher.group(1));
                if (resetDiffLine) {
                    diffLine = 1;
                    resetDiffLine = false;
                } else {
                    diffLine++;
                }
                continue;
            }

            if (line.startsWith("+")) {
                // Last diff object needs.
                // Remove first character because first character represents sign.
                diffs.get(diffs.size() - 1).getAddLines().add(new Diff.AddLine(fileLine, diffLine, line.substring(1)));
            }

            if (line.startsWith("+") || line.startsWith(" ")) {
                fileLine++;
            }
            diffLine++;
        }

        return diffs;
    }

    public List<Suggestion> getSuggestions(List<Diff> added) throws IOException {
        return spellCheck(added);
    }

    public List<String> getRepositoryDictionary(Event event, Token token) {
        Event.Head head = event.getPullRequest().getHead();
        String contentsUrl = head.getRepo().getContentsUrl();
        String ref = head.getRef();

        List<String> dict = new ArrayList<>();
        repository.getRawContent(contentsUrl, "typofixer.dic", ref, token.getToken())
                .ifPresent(FuntionalExtension.Try(s -> IOUtils.readLines(new StringReader(s)),
                        (e, s) -> log.error("IOUtils#readLines throws an error.", e)));
        return dict;
    }


    public TypoCheckerService setDictionary(List<String> dictionary) {
        for (Rule rule : jLanguageTool.getAllActiveRules()) {
            if (rule instanceof SpellingCheckRule) {
                ((SpellingCheckRule) rule).acceptPhrases(dictionary);
            }
        }
        return this;
    }

    private List<Suggestion> spellCheck(final List<Diff> added) throws IOException {
        List<Suggestion> suggestions = new ArrayList<>();
        for (Diff diff : added) {
            List<Diff.AddLine> addLines = diff.getAddLines();
            for (Diff.AddLine line : addLines) {
                String text = line.getLineContent();
                List<RuleMatch> matches = jLanguageTool.check(text);
                for (RuleMatch match : matches) {
                    suggestions.add(new Suggestion(diff.getPath(), text, line.getFileLine(), line.getDiffLine(), match.getFromPos(), match.getToPos(), match.getSuggestedReplacements()));
                }
            }
        }
        return suggestions;
    }
}