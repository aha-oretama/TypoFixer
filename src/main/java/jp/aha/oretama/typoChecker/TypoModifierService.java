package jp.aha.oretama.typoChecker;

import jp.aha.oretama.typoChecker.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author aha-oretama
 */
@Service
@RequiredArgsConstructor
public class TypoModifierService {

    private static final Pattern PATTERN =Pattern.compile("- \\[x\\] (.*)",Pattern.MULTILINE);

    public Map<Boolean, String> getModifications(Event event) {

        String after = event.getChanges().getBody().getFrom();
        String before = event.getComment().getBody();

        List<String> afterItems = getCheckedItems(after);
        List<String> beforeItems = getCheckedItems(before);

        Map<Boolean, String> added = new HashMap<>();
        // Multiple checked items are not meant at all.
        if(afterItems.size() > 1 || beforeItems.size() > 1) {
            return added;
        }

        // Key is true if checked items are added.
        if (afterItems.size() > beforeItems.size()) {
            added.put(true, afterItems.stream().filter(s -> !beforeItems.contains(s)).findFirst().get());
        } else if (afterItems.size() < beforeItems.size()) {
            added.put(false, beforeItems.stream().filter(s -> !afterItems.contains(s)).findFirst().get());
        }
        // Do nothing if there are no changes.
        return added;
    }

    private List<String> getCheckedItems(String body) {
        Matcher matcher = PATTERN.matcher(body);
        List<String> checkedItems = new ArrayList<>();

        if(matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                checkedItems.add(matcher.group(i));
            }
        }
        return checkedItems;
    }
}
