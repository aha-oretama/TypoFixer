package jp.aha.oretama.typoFixer.parser;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.modules.ModuleDeclaration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
public class JavaParser implements Parser {

    private List<? extends Class<? extends Node>> targets = Arrays.asList(
            AnnotationDeclaration.class,
            AnnotationMemberDeclaration.class,
            BodyDeclaration.class,
            CallableDeclaration.class,
            ClassOrInterfaceDeclaration.class,
            ConstructorDeclaration.class,
            EnumConstantDeclaration.class,
            EnumDeclaration.class,
            FieldDeclaration.class,
            InitializerDeclaration.class,
            MethodDeclaration.class,
            ModuleDeclaration.class,
            TypeDeclaration.class);

    private List<? extends Class<? extends Node>> nonTargets = Arrays.asList(
            ImportDeclaration.class,
            PackageDeclaration.class
    );

    private final CompilationUnit unit;
    private final Map<Integer, List<Node>> map = new HashMap<>();

    public JavaParser(String source) {
        unit = com.github.javaparser.JavaParser.parse(source);
    }


    @Override
    public JavaParser parseLines(List<Integer> lines) {
        for (Integer i : lines) {
            ArrayList<Node> list = new ArrayList<>();
            addAllDeepChildNodes(list, unit.getChildNodes(), i);
            map.put(i, list);
        }
        return this;
    }

    @Override
    public List<Integer> getTargetLines() {
        Set<Integer> filtered = new HashSet<>();
        for (Map.Entry<Integer, List<Node>> entry : map.entrySet()) {
            // Empty means comment line.
            if (entry.getValue().isEmpty()) {
                filtered.add(entry.getKey());
                continue;
            }

            for (Node node : entry.getValue()) {
                if (nonTargets.stream().anyMatch(o -> o.isAssignableFrom(node.getClass()))) {
                    continue;
                }
                filtered.add(entry.getKey());
            }
        }
        return new ArrayList<>(filtered);
    }

    private void addAllDeepChildNodes(List<Node> list, List<Node> nodes, Integer line) {
        List<Node> filteredNodes = nodes.stream()
                .filter(node -> node.getRange().isPresent())
                .filter(node -> {
                    Range range = node.getRange().get();
                    return range.begin.line <= line && line <= range.end.line;
                }).collect(Collectors.toList());

        if (filteredNodes.isEmpty()) {
            return;
        }

        for (Node node : filteredNodes) {
            if (nonTargets.stream().anyMatch(o -> o.isAssignableFrom(node.getClass()))) {
                list.add(node);
            } else if (node.getChildNodes().isEmpty()) {
                list.add(node);
            } else {
                addAllDeepChildNodes(list, node.getChildNodes(), line);
            }
        }
    }
}
