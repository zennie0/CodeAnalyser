package com.codeinsight.analyzer;

import com.codeinsight.dto.Finding;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.CatchClause;
import java.util.*;

/** Detects swallowed exceptions that hide failures from callers and logs. */
public class EmptyCatchAnalyzer {
    /** Reports catch blocks containing no executable statements. */
    public List<Finding> analyze(CompilationUnit unit) {
        List<Finding> findings = new ArrayList<>();
        for (CatchClause clause : unit.findAll(CatchClause.class)) if (clause.getBody().getStatements().isEmpty())
            findings.add(new Finding("EMPTY_CATCH", clause.getRange().map(r -> r.begin.line).orElse(0), "Empty catch block",
                    "The exception is silently ignored, making production failures difficult to diagnose.",
                    "Log the exception, handle it deliberately, or rethrow a meaningful exception.", "error"));
        return findings;
    }
}
