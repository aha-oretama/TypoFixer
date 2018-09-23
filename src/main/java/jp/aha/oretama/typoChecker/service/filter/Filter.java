package jp.aha.oretama.typoChecker.service.filter;

import jp.aha.oretama.typoChecker.model.Diff;

import java.util.List;

/**
 * @author aha-oretama
 */
public interface Filter {
    public void filtering(List<Diff> added);
}
