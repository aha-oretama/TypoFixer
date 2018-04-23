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
    // That modification is not added means to revert the word from correct to typo.
    private boolean added;
    private String correct;

}
