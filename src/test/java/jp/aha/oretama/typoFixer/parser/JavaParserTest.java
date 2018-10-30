package jp.aha.oretama.typoFixer.parser;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author aha-oretama
 */
@Disabled("TODO, Not implement")
public class JavaParserTest {

    @Test
    public void ThisIsTest() throws IOException {
        String source = FileUtils.readFileToString(new File("src/test/resources/testData/Tmp"), StandardCharsets.UTF_8);
        List<Integer> targetLines = new JavaParser(source).parseLines(IntStream.rangeClosed(1, 573).boxed().collect(Collectors.toList())).getTargetLines();
    }
}