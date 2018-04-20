package jp.aha.oretama.typoChecker.model;

import lombok.Data;
import lombok.Value;

/**
 * @author aha-oretama
 */
@Data
public class Modification {
    private String typo;
    private int line;
    private boolean added;
    private String correct;

}
