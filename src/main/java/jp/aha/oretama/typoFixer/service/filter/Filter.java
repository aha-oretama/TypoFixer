package jp.aha.oretama.typoFixer.service.filter;

import jp.aha.oretama.typoFixer.model.Diff;

import java.util.List;

/**
 * @author aha-oretama
 */
public interface Filter {
    public void filtering(List<Diff> added);
}
