package com.codeinsight.analyzer;

import com.codeinsight.dto.Finding;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.*;
import java.util.*;

/** Finds loops inside other loops; nesting is a common performance hotspot. */
public class NestedLoopAnalyzer {
    private static final Class<?>[] LOOP_TYPES = { ForStmt.class, ForEachStmt.class, WhileStmt.class, DoStmt.class };

    /** Reports every loop whose parent chain already contains a loop. */
    public List<Finding> analyze(Node root) {
        List<Finding> output = new ArrayList<>();
        root.findAll(Statement.class).stream().filter(this::isLoop).forEach(loop -> {
            int depth = loopDepth(loop);
            if (depth > 1)
                output.add(new Finding("NESTED_LOOP", line(loop), "Nested loop (depth " + depth + ")",
                        "This loop runs inside another loop and may multiply execution time.",
                        "Move invariant work outside the loop, use a lookup structure, or reduce iterations.",
                        "warning"));
        });
        return output;
    }

    /** Counts containing loop statements to determine nesting depth. */
    public int loopDepth(Node node) {
        int depth = 0;
        for (Node n = node; n != null; n = n.getParentNode().orElse(null))
            if (n instanceof Statement s && isLoop(s))
                depth++;
        return depth;
    }

    private boolean isLoop(Statement node) {
        return Arrays.stream(LOOP_TYPES).anyMatch(c -> c.isInstance(node));
    }

    private int line(Node node) {
        return node.getRange().map(r -> r.begin.line).orElse(0);
    }
}
