package jp.aha.oretama.typoFixer.service;

import jp.aha.oretama.typoFixer.model.Diff;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author aha-oretama
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TypoCheckerServiceTest {

    TypoCheckerService service = new TypoCheckerService(new JLanguageTool(new AmericanEnglish()), null);

    private String rawDiff = "diff --git a/README.md b/README.md\n" +
            "index f247396..93a9c32 100644\n" +
            "--- a/README.md\n" +
            "+++ b/README.md\n" +
            "@@ -1 +1 @@\n" +
            "-# public-repo\n" +
            "+Nobody reads\n" +
            "\\ No newline at end of file";

    private String rawDiff2 = "diff --git a/typo-fixer.json b/typo-fixer.json\n" +
            "new file mode 100644\n" +
            "index 0000000..f85f9be\n" +
            "--- /dev/null\n" +
            "+++ b/typo-fixer.json\n" +
            "@@ -0,0 +1,3 @@\n" +
            "+{\n" +
            "+   \"extensions\": [\".md\"] \n" +
            "+}" +
            "diff --git a/README.md b/README.md\n" +
            "index 00bcb6e..e41e46f 100644\n" +
            "--- a/README.md\n" +
            "+++ b/README.md\n" +
            "@@ -1 +1,5 @@\n" +
            "-# test\n" +
            "\\ No newline at end of file\n" +
            "+# test\n" +
            "+\n" +
            "+This is test for typo fixer.\n" +
            "+This is typooo.\n" +
            "+This is alsonn typo.";

    @Test
    public void getSuggestionsFromRawDiff() {
        List<Diff> result = service.getAdded(rawDiff);

        assertEquals(1, result.size());
        Diff diff = result.get(0);
        assertEquals("README.md", diff.getPath());
        assertEquals("Nobody reads", diff.getAdded().get(0));
    }

    @Test
    public void getSuggestionsFromRawDiff2() {
        List<Diff> result = service.getAdded(rawDiff2);

        assertEquals(2, result.size());

        Diff diff0 = result.get(0);
        assertEquals("typo-fixer.json", diff0.getPath());
        assertEquals("   \"extensions\": [\".md\"] ", diff0.getAdded().get(2));

        Diff diff1 = result.get(1);
        assertEquals("README.md", diff1.getPath());
        assertEquals("# test", diff1.getAdded().get(1));
    }

}