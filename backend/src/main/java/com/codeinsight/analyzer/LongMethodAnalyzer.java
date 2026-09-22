package com.codeinsight.analyzer;

import com.codeinsight.dto.Finding;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import java.util.*;

/** Flags methods that exceed a readable, maintainable line-count threshold. */
public class LongMethodAnalyzer {
    private static final int LIMIT = 35;
    /** Finds methods spanning more than 35 lines. */
    public List<Finding> analyze(CompilationUnit unit) {
        List<Finding> findings = new ArrayList<>();
        for (MethodDeclaration method : unit.findAll(MethodDeclaration.class)) method.getRange().ifPresent(r -> {
            int length = r.end.line - r.begin.line + 1;
            if (length > LIMIT) findings.add(new Finding("LONG_METHOD", r.begin.line, "Long method: " + method.getNameAsString(),
                    "This method is " + length + " lines long (recommended maximum: " + LIMIT + ").",
                    "Extract focused helper methods and give each one a single responsibility.", "info"));
        });
        return findings;
    }
}
