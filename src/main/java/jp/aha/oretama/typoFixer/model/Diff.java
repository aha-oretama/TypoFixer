package jp.aha.oretama.typoFixer.model;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.apache.commons.io.FilenameUtils;

import java.util.List;

/**
 * @author aha-oretama
 */
public class Diff {

    public Diff(String path, List<AddLine> addLines) {
        this.path = path;
        this.addLines = addLines;
    }

    @Getter
    private String path;
    @Getter
    @Setter
    private List<AddLine> addLines;
    @Getter
    @Setter
    private String content;

    public String getExtension() {
        return "." + FilenameUtils.getExtension(path);
    }

    @Value
    public static class AddLine {
        // fileLine is committed file's line number.
        private Integer fileLine;
        // diffLine is line number which GitHub displays in diff page and include all of +, - , not change lines.
        private Integer diffLine;
        private String lineContent;
    }
}