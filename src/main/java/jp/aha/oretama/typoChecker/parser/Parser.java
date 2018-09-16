package jp.aha.oretama.typoChecker.parser;

import java.util.Iterator;
import java.util.List;

/**
 * @author aha-oretama
 */
public interface Parser {
    Parser parseLines(List<Integer> iterator);

    List<Integer> getTargetLines();
}
