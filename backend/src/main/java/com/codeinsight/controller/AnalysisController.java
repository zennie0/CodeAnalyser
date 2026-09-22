package com.codeinsight.controller;

import com.codeinsight.analyzer.CodeAnalysisService;
import com.codeinsight.dto.AnalysisResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/** Exposes pasted-source and .java file analysis endpoints to the frontend. */
@RestController
@RequestMapping("/api/analyze")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalysisController {
    private final CodeAnalysisService service = new CodeAnalysisService();

    /** Analyzes Java source submitted as JSON. */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public AnalysisResponse analyzeText(@RequestBody Map<String, String> request) { return service.analyze(request.getOrDefault("code", "")); }

    /** Analyzes exactly one uploaded Java file; folders and other formats are rejected. */
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnalysisResponse analyzeFile(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty() || file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".java"))
            throw new IllegalArgumentException("Please upload one non-empty .java file.");
        return service.analyze(new String(file.getBytes(), StandardCharsets.UTF_8));
    }

    /** Converts parser and input errors into useful client-facing messages. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleError(Exception error) {
        return ResponseEntity.badRequest().body(Map.of("error", error.getMessage() == null ? "Could not analyze this source." : error.getMessage()));
    }
}
