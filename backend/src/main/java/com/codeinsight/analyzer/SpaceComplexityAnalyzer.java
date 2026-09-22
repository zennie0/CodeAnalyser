package com.codeinsight.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;

/** Estimates auxiliary space from explicit arrays and object allocations. */
public class SpaceComplexityAnalyzer {
    /** Returns O(n) when source builds arrays/collections, otherwise O(1) for auxiliary storage. */
    public String analyze(CompilationUnit unit) {
        boolean allocates = !unit.findAll(ArrayCreationExpr.class).isEmpty() || unit.findAll(ObjectCreationExpr.class).stream()
                .anyMatch(o -> o.getTypeAsString().matches(".*(List|Set|Map|Queue|Stack|Buffer).*"));
        return allocates ? "O(n)" : "O(1)";
    }
}
