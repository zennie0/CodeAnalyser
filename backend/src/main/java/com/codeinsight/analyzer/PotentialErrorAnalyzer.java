package com.codeinsight.analyzer;

import com.codeinsight.dto.Finding;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import java.util.*;

/** Highlights calls that commonly fail when their input is null. */
public class PotentialErrorAnalyzer {
    /** Finds potentially unsafe chained method calls for review. */
    public List<Finding> analyze(CompilationUnit unit) {
        List<Finding> findings = new ArrayList<>();
        for (MethodCallExpr call : unit.findAll(MethodCallExpr.class)) if (call.getScope().isPresent() &&
                Set.of("get", "trim", "length", "toString").contains(call.getNameAsString()))
            findings.add(new Finding("POTENTIAL_NULL", call.getRange().map(r -> r.begin.line).orElse(0), "Review possible null access",
                    "This call can throw NullPointerException if its receiver is null.",
                    "Validate the value first, use Optional where appropriate, or provide a safe default.", "info"));
        return findings;
    }
}
