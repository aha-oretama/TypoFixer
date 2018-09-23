package jp.aha.oretama.typoFixer.model;

import lombok.Getter;
import lombok.Setter;
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
    @Getter
    @Setter
    private String content;

    public String getExtension() {
        return "." + FilenameUtils.getExtension(path);
    }
}