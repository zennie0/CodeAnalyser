package com.codeinsight.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.*;

/** Estimates Big-O by inspecting the deepest syntactic loop nesting. */
public class TimeComplexityAnalyzer {
    /** Gives a conservative structural estimate; it deliberately never claims exact runtime. */
    public String analyze(CompilationUnit unit) {
        NestedLoopAnalyzer nesting = new NestedLoopAnalyzer();
        int max = unit.findAll(Statement.class).stream()
                .filter(s -> s instanceof ForStmt || s instanceof ForEachStmt || s instanceof WhileStmt || s instanceof DoStmt)
                .mapToInt(nesting::loopDepth).max().orElse(0);
        if (max == 0) return "O(1)";
        if (max == 1) return "O(n)";
        return "O(n" + (max == 2 ? "²" : "^" + max) + ")";
    }
}
