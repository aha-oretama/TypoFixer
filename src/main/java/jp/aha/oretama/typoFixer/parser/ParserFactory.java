package jp.aha.oretama.typoFixer.parser;

import org.springframework.stereotype.Component;

/**
 * @author aha-oretama
 */
@Component
public class ParserFactory {

    public Parser create(String extension, String content) {
        switch (extension) {
            case ".java":
                return new JavaParser(content);
            default:
                return new NoopParser(content);
        }
    }
}
