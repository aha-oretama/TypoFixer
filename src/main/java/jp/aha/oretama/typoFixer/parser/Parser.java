package jp.aha.oretama.typoFixer.parser;

import java.util.List;

/**
 * @author aha-oretama
 */
public interface Parser {
    Parser parseLines(List<Integer> iterator);

    List<Integer> getTargetLines();
}
