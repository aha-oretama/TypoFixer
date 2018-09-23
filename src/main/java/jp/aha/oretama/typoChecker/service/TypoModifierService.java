package jp.aha.oretama.typoChecker.service;

import jp.aha.oretama.typoChecker.model.Event;
import jp.aha.oretama.typoChecker.model.Modification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author aha-oretama
 */
@Service
@RequiredArgsConstructor
public class TypoModifierService {

    private static final Pattern CHECK_PATTERN = Pattern.compile("- \\[x\\] (.*)", Pattern.MULTILINE);
    private static final Pattern TYPO_LINE_PATTERN = Pattern.compile("Typo\\? \"(.+)\" at (\\d+) line\\.", Pattern.MULTILINE);

    public Optional<Modification> getModification(Event event) {
        String before = event.getChanges().getBody().getFrom();
        String after = event.getComment().getBody();

        List<String> typoAndLine = getTypoAndLine(after);
        // Not the target comment
        if (typoAndLine.size() != 2) {
            return Optional.empty();
        }

        List<String> afterItems = getCheckedItems(after);
        List<String> beforeItems = getCheckedItems(before);

        Map<Boolean, String> added = new HashMap<>();
        // Multiple checked items are not meant at all. Do nothing if there are no changes.
        if (afterItems.size() > 1 || beforeItems.size() > 1 || afterItems.size() == beforeItems.size()) {
            return Optional.empty();
        }

        Modification modification = new Modification();
        modification.setTypo(typoAndLine.get(0));
        modification.setLine(Integer.valueOf(typoAndLine.get(1)));
        // Key is true if checked items are added.
        boolean isAdded = afterItems.size() > beforeItems.size();
        List<String> greater = isAdded ? afterItems : beforeItems;
        List<String> less = isAdded ? beforeItems : afterItems;
        modification.setAdded(isAdded);
        modification.setCorrect(greater.stream().filter(o -> !less.contains(o)).findFirst().get());
        return Optional.of(modification);
    }

    private List<String> getCheckedItems(String body) {
        Matcher matcher = CHECK_PATTERN.matcher(body);
        List<String> checkedItems = new ArrayList<>();

        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                checkedItems.add(matcher.group(i));
            }
        }
        return checkedItems;
    }

    private List<String> getTypoAndLine(String body) {
        Matcher matcher = TYPO_LINE_PATTERN.matcher(body);

        List<String> list = new ArrayList<>();
        if (matcher.find()) {
            list.add(matcher.group(1));
            list.add(matcher.group(2));
        }
        return list;
    }
}
