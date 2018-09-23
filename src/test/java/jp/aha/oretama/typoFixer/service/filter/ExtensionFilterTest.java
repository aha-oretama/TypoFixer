package jp.aha.oretama.typoFixer.service.filter;

import jp.aha.oretama.typoFixer.model.Diff;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author aha-oretama
 */
public class ExtensionFilterTest {

    List<Diff> diffs = new ArrayList<>();

    @Before
    public void setUp() {
        diffs.add(new Diff("src/test/java/jp/aha/oretama/typoFixer/service/filter/ExtensionFilterTest.java", null));
        diffs.add(new Diff("README.md", null));
        diffs.add(new Diff("pom.xml", null));
    }

    @Test
    public void notFilteringReturnAllFiles() {
        // Arrange
        Filter filter = new ExtensionFilter(new ArrayList<>());

        // Act
        filter.filtering(diffs);

        // Assert
        assertEquals(3, diffs.size());
    }

    @Test
    public void filteringByOneReturnOneFileExtension() {
        // Arrange
        Filter filter = new ExtensionFilter(Collections.singletonList(".java"));

        // Act
        filter.filtering(diffs);

        // Assert
        assertEquals(1, diffs.size());
        assertEquals("src/test/java/jp/aha/oretama/typoFixer/service/filter/ExtensionFilterTest.java", diffs.get(0).getPath());
    }

    @Test
    public void filteringAllByReturnAll() {
        // Arrange
        Filter filter = new ExtensionFilter(Arrays.asList(".java", ".md", ".xml"));

        // Act
        filter.filtering(diffs);

        // Assert
        assertEquals(3, diffs.size());
    }

}