package jp.aha.oretama.typoFixer.language;

import org.languagetool.language.English;
import org.languagetool.rules.Rule;
import org.languagetool.rules.en.MorfologikAmericanSpellerRule;
import org.languagetool.tokenizers.WordTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author aha-oretama
 */
public class CodingEnglish extends English {

    private WordTokenizer wordTokenizer;

    @Override
    public String[] getCountries() {
        return new String[]{"US"};
    }

    @Override
    public String getName() {
        return "English (US)";
    }

    @Override
    public WordTokenizer getWordTokenizer() {
        if (this.wordTokenizer == null) {
            this.wordTokenizer = new CodingWordTokenizer();
        }

        return this.wordTokenizer;
    }


    @Override
    public List<Rule> getRelevantRules(ResourceBundle messages) throws IOException {
        List<Rule> rules = new ArrayList<>();
        rules.addAll(super.getRelevantRules(messages));
        rules.add(new MorfologikAmericanSpellerRule(messages, this));
        return rules;
    }

}
