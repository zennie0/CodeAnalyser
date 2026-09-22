package com.codeinsight.analyzer;

import com.codeinsight.dto.AnalysisResponse;
import com.codeinsight.dto.Finding;
import com.codeinsight.dto.SyntaxError;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.Range;
import java.util.*;

/** Coordinates the independent analyzers into one predictable API response. */
public class CodeAnalysisService {
    /** Parses Java source once, then runs every specialized analyzer against the AST. */
    public AnalysisResponse analyze(String source) {
        if (source == null || source.isBlank()) return syntaxErrorResponse(source, List.of(
                new SyntaxError(1, 1, "No Java source was provided.", "Paste Java code or upload one non-empty .java file.")));
        CompilationUnit unit;
        try {
            unit = StaticJavaParser.parse(source);
        } catch (ParseProblemException error) {
            List<SyntaxError> errors = error.getProblems().stream().map(problem -> {
                int line = problem.getLocation().flatMap(location -> location.toRange()).map(range -> range.begin.line).orElse(0);
                int column = problem.getLocation().flatMap(location -> location.toRange()).map(range -> range.begin.column).orElse(0);
                return new SyntaxError(line, column, cleanMessage(problem.getMessage()), syntaxSuggestion(problem.getMessage()));
            }).toList();
            return syntaxErrorResponse(source, errors);
        }
        

        List<Finding> findings = new ArrayList<>();
        findings.addAll(new NestedLoopAnalyzer().analyze(unit));
        findings.addAll(new LongMethodAnalyzer().analyze(unit));
        findings.addAll(new EmptyCatchAnalyzer().analyze(unit));
        findings.addAll(new PotentialErrorAnalyzer().analyze(unit));
        findings.sort(Comparator.comparingInt(Finding::line));
        int nested = (int) findings.stream().filter(f -> f.type().equals("NESTED_LOOP")).count();
        int score = Math.max(1, 10 - nested * 2 - (int) findings.stream().filter(f -> f.severity().equals("error")).count() * 2
                - (int) findings.stream().filter(f -> f.type().equals("LONG_METHOD")).count());
        String label = score >= 8 ? "Clean" : score >= 5 ? "Needs attention" : "High risk";
        List<String> highlights = findings.isEmpty() ? List.of("No structural issues detected. Nice work!") : findings.stream().map(Finding::suggestion).distinct().limit(4).toList();
        return new AnalysisResponse(new TimeComplexityAnalyzer().analyze(unit), new SpaceComplexityAnalyzer().analyze(unit), score,
                label, (int) source.lines().filter(s -> !s.trim().isEmpty()).count(), nested, findings, highlights, List.of());
    }

    /** Builds a consistent response when JavaParser cannot form a Java compilation unit. */
    private AnalysisResponse syntaxErrorResponse(String source, List<SyntaxError> errors) {
        int lines = source == null || source.isEmpty() ? 0 : (int) source.lines().count();
        return new AnalysisResponse(null, null, 0, "Syntax error", lines, 0, List.of(),
                List.of("Fix the syntax errors below, then analyze the code again."), errors);
    }

    /** Removes parser formatting noise so the user sees a compact, readable error message. */
    private String cleanMessage(String message) { return message.replaceAll("\\s+", " ").trim(); }

    /** Chooses a practical next step for common JavaParser failures and non-Java input. */
    private String syntaxSuggestion(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("end of file") || lower.contains("expected"))
            return "Check for a missing closing brace, parenthesis, semicolon, or quote near this line.";
        if (lower.contains("encountered") || lower.contains("unexpected"))
            return "This is not valid Java syntax here. Check spelling, punctuation, and whether the pasted code is Java.";
        return "Use valid Java source code. If this came from another language, paste or upload a .java file instead.";
    }
}
