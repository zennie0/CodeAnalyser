package com.codeinsight.dto;

import java.util.List;

/** The complete result delivered to the React client. */
public record AnalysisResponse(String timeComplexity, String spaceComplexity, int score, String scoreLabel,
                               int linesOfCode, int nestedLoopCount, List<Finding> findings,
                               List<String> highlights,  List<SyntaxError> syntaxErrors) { }
