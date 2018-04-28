package jp.aha.oretama.typoChecker.language;

import org.languagetool.tokenizers.WordTokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author aha-oretama
 */
public class CodingWordTokenizer extends WordTokenizer {

    @Override
    public String getTokenizingCharacters() {
        return super.getTokenizingCharacters() + "–_@";
    }


    @Override
    public List<String> tokenize(String text) {
        List<String> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(text, this.getTokenizingCharacters(), true);

        while(tokenizer.hasMoreElements()) {
            Collections.addAll(list, tokenizer.nextToken().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"));
        }

        return this.joinEMailsAndUrls(list);
    }
}
