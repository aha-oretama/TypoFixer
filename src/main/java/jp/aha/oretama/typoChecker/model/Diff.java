package jp.aha.oretama.typoChecker.model;

import lombok.Value;

import java.util.Map;

/**
 * @author aha-oretama
 */
@Value
public class Diff {
    private String path;
    private Map<Integer, String> added;
}