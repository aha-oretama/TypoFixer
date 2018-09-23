package jp.aha.oretama.typoFixer.service.filter;

import jp.aha.oretama.typoFixer.model.Diff;

import java.util.List;

/**
 * @author aha-oretama
 */
public class ExtensionFilter implements Filter {

    private final List<String> extensions;

    public ExtensionFilter(List<String> extensions) {
        this.extensions = extensions;
    }

    @Override
    public void filtering(List<Diff> added) {
        if (extensions.isEmpty()) {
            return;
        }
        added.removeIf(diff -> !this.extensions.contains(diff.getExtension()));
    }
}
