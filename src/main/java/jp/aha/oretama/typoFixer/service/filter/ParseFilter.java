package jp.aha.oretama.typoFixer.service.filter;

import jp.aha.oretama.typoFixer.model.Diff;
import jp.aha.oretama.typoFixer.parser.Parser;
import jp.aha.oretama.typoFixer.parser.ParserFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
public class ParseFilter implements Filter {
    private final ParserFactory factory;

    public ParseFilter() {
        factory = new ParserFactory();
    }

    @Override
    public void filtering(List<Diff> added) {
        // Execute AST.
        for (Diff diff : added) {
            Parser parser = factory.create(diff.getExtension(), diff.getContent());
            List<Integer> targetLines = parser
                    .parseLines(new ArrayList<>(diff.getAdded().keySet()))
                    .getTargetLines();

            List<Integer> nonTargetLines = diff.getAdded().keySet().stream()
                    .filter(integer -> !targetLines.contains(integer)).collect(Collectors.toList());

            for (Integer line : nonTargetLines) {
                diff.getAdded().remove(line);
            }
        }
    }
}
