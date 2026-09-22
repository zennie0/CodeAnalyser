package com.codeinsight.dto;

/** A JavaParser error enriched with a source location and an actionable fix. */
public record SyntaxError(int line, int column, String message, String suggestion) { }
