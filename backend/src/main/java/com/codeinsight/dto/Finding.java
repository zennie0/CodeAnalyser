package com.codeinsight.dto;

/** A precise issue or observation tied to a source line. */
public record Finding(String type, int line, String title, String detail, String suggestion, String severity) { }
