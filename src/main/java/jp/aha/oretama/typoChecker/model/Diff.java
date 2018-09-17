package jp.aha.oretama.typoChecker.model;

import jp.aha.oretama.typoChecker.parser.Parser;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.apache.commons.io.FilenameUtils;

import java.util.Map;

/**
 * @author aha-oretama
 */
public class Diff {
    public Diff(String path, Map<Integer, String> added) {
        this.path = path;
        this.added = added;
    }

    @Getter
    private String path;
    @Getter
    private Map<Integer, String> added;
    @Setter
    private Parser parser;
    public String getExtension() {
        return FilenameUtils.getExtension(path);
    }
}