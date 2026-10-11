# CodeAnalyser

**A Java source-code analysis tool built with Spring Boot and React.**

CodeAnalyser is a web-based application designed to inspect Java source code and identify potential code-quality issues through automated static analysis. It combines a Spring Boot backend with a React-based interface to make code analysis more accessible and understandable.

## Overview

Writing maintainable code involves more than making a program work. CodeAnalyser focuses on identifying patterns that may indicate readability, maintainability, or reliability concerns, helping developers recognize areas of code that could benefit from improvement.

The backend uses JavaParser to parse Java source code and apply custom analysis rules. The frontend provides a visual interface for interacting with the analysis system and presenting its results.

## Key Features

- **Nested Loop Detection** — Identifies nested loops that may warrant closer inspection for complexity and performance implications.
- **Long Method Detection** — Flags lengthy methods that may benefit from refactoring into smaller, more focused units.
- **Empty Catch Block Detection** — Highlights empty exception handlers that may silently suppress errors.
- **Potential Error Detection** — Applies custom analysis rules to identify suspicious code patterns that deserve further review.
- **Web-Based Interface** — Provides a React frontend for interacting with the code-analysis functionality.

## Technology Stack

| Component | Technologies |
|---|---|
| Backend | Java, Spring Boot |
| Build Tool | Maven |
| Static Analysis | JavaParser, custom analysis rules |
| Frontend | React, JavaScript |
| Development & Deployment | Git, GitHub, Docker |

## Architecture

CodeAnalyser separates its analysis logic from its user interface. The Spring Boot backend processes Java source code and runs the analysis rules, while the React frontend provides the interface for submitting code and viewing the results.

This separation supports independent development of the backend and frontend.

## Project Goals

- Explore static analysis techniques for Java source code.
- Identify code patterns associated with maintainability and potential reliability concerns.
- Apply object-oriented design to build modular analysis components.
- Integrate a Java backend with a modern web interface.
- Package the backend for containerized deployment.

## Future Scope

Potential improvements include additional analysis rules, more detailed findings, severity classification, and richer explanations of detected issues.

---

**CodeAnalyser** — Exploring code quality through automated static analysis.