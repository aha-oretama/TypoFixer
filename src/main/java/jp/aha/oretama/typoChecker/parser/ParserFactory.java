package jp.aha.oretama.typoChecker.parser;

import jp.aha.oretama.typoChecker.model.Diff;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

/**
 * @author aha-oretama
 */
@Component
public class ParserFactory {

    public Parser create(String path, String content) {
        String extension = FilenameUtils.getExtension(path);
        switch (extension) {
            case ".java":
                return new JavaParser(content);
            default:
                return new NoopParser(content);
        }
    }
}
