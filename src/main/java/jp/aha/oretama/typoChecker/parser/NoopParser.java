package jp.aha.oretama.typoChecker.parser;

import java.util.Iterator;
import java.util.List;

/**
 * @author aha-oretama
 */
public class NoopParser implements Parser {

    private final String source;
    private List<Integer> lines;

    public NoopParser(String source) {
        this.source = source;
    }

    @Override
    public Parser parseLines(List<Integer> lines) {
        this.lines = lines;
        return this;
    }

    @Override
    public List<Integer> getTargetLines() {
        return lines;
    }
}
