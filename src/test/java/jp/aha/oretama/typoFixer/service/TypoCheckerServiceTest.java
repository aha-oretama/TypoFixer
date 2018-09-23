package jp.aha.oretama.typoFixer.service;

import jp.aha.oretama.typoFixer.model.Diff;
import jp.aha.oretama.typoFixer.service.TypoCheckerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;
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

    @Test
    public void getSuggestionsFromRawDiff() throws Exception {

        Method method = TypoCheckerService.class.getDeclaredMethod("getAdded", String.class);
        method.setAccessible(true);

        List<Diff> result = (List<Diff>) method.invoke(service, rawDiff);

        assertEquals(1, result.size());
        Diff diff = result.get(0);
        assertEquals("README.md", diff.getPath());
        // TODO: Is position from 0 or 1? Confirm GitHub api.
        diff.getAdded().forEach((integer, s) -> {
                    assertEquals(0, integer.intValue());
                    assertEquals("Nobody reads", s);
                }
        );
    }
}