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

    private TypoCheckerService service = new TypoCheckerService(new JLanguageTool(new AmericanEnglish()), null);

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

    private String rawDiff3 = "diff --git a/README.md b/README.md\n" +
            "index 8cacdfa..e41e46f 100644\n" +
            "--- a/README.md\n" +
            "+++ b/README.md\n" +
            "@@ -1,4 +1,5 @@\n" +
            " # test\n" +
            " \n" +
            "-This is test repository.\n" +
            "-Ignore this repostiosy.\n" +
            "+This is test for typo fixer.\n" +
            "+This is typooo.\n" +
            "+This is alsonn typo.";

    private String rawDiff4 = "diff --git a/README.md b/README.md\n" +
            "index 3398d7a..660bb3a 100644\n" +
            "--- a/README.md\n" +
            "+++ b/README.md\n" +
            "@@ -1,2 +1 @@\n" +
            "-# test\n" +
            "-aaa\n" +
            "+# test3\n" +
            "\\ No newline at end of file";

    @Test
    public void getSuggestionsFromRawDiff() {
        List<Diff> result = service.getAdded(rawDiff);

        assertEquals(1, result.size());
        Diff diff = result.get(0);
        assertEquals(1, diff.getAddLines().size());
        Diff.AddLine addLine = diff.getAddLines().get(0);
        assertEquals("README.md", diff.getPath());
        assertEquals("Nobody reads", addLine.getLineContent());
        assertEquals(1, addLine.getFileLine().intValue());
        assertEquals(2, addLine.getDiffLine().intValue());
    }

    @Test
    public void getSuggestionsFromRawDiff2() {
        List<Diff> result = service.getAdded(rawDiff2);

        assertEquals(2, result.size());
        Diff diff0 = result.get(0);
        assertEquals("typo-fixer.json", diff0.getPath());
        assertEquals(3, diff0.getAddLines().size());
        Diff.AddLine addLine0 = diff0.getAddLines().get(1);
        assertEquals("   \"extensions\": [\".md\"] ", addLine0.getLineContent());
        assertEquals(2, addLine0.getFileLine().intValue());
        assertEquals(2, addLine0.getDiffLine().intValue());

        Diff diff1 = result.get(1);
        assertEquals("README.md", diff1.getPath());
        assertEquals(5, diff1.getAddLines().size());
        Diff.AddLine addLine1 = diff1.getAddLines().get(2);
        assertEquals("This is test for typo fixer.", addLine1.getLineContent());
        assertEquals(3, addLine1.getFileLine().intValue());
        assertEquals(5, addLine1.getDiffLine().intValue());
    }

    @Test
    public void getSuggestionsFromRawDiff3() {
        List<Diff> result = service.getAdded(rawDiff3);

        assertEquals(1, result.size());
        Diff diff = result.get(0);
        assertEquals("README.md", diff.getPath());

        assertEquals(3, diff.getAddLines().size());
        Diff.AddLine addLine = diff.getAddLines().get(0);
        assertEquals("This is test for typo fixer.", addLine.getLineContent());
        assertEquals(3, addLine.getFileLine().intValue());
        assertEquals(5, addLine.getDiffLine().intValue());
    }

    @Test
    public void getSuggestionsFromRawDiff4() {
        List<Diff> result = service.getAdded(rawDiff4);

        assertEquals(1, result.size());
        Diff diff = result.get(0);
        assertEquals("README.md", diff.getPath());

        assertEquals(1, diff.getAddLines().size());
        Diff.AddLine addLine = diff.getAddLines().get(0);
        assertEquals("# test3", addLine.getLineContent());
        assertEquals(1, addLine.getFileLine().intValue());
        assertEquals(3, addLine.getDiffLine().intValue());
    }
}