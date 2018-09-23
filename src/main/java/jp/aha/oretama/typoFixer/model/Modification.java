package jp.aha.oretama.typoFixer.model;

import lombok.Data;

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
