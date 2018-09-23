package jp.aha.oretama.typoFixer.service.filter;

import jp.aha.oretama.typoFixer.model.Diff;
import jp.aha.oretama.typoFixer.parser.Parser;
import jp.aha.oretama.typoFixer.parser.ParserFactory;

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
                    .parseLines(diff.getAddLines().stream().map(Diff.AddLine::getFileLine).collect(Collectors.toList()))
                    .getTargetLines();

            List<Integer> nonTargetLines = diff.getAddLines().stream().map(Diff.AddLine::getFileLine)
                    .filter(integer -> !targetLines.contains(integer)).collect(Collectors.toList());


            diff.getAddLines().removeIf(addLine -> nonTargetLines.contains(addLine.getFileLine()));
        }
    }
}
