# CodeAnalyser

Java source-code analysis with a Spring Boot + Maven backend and futuristic React UI.

## Run it

1. Start the API: `cd backend` then `mvn spring-boot:run`.
2. Start the UI in another terminal: `cd frontend`, `npm install`, then `npm run dev`.
3. Open the Vite address shown in the terminal (normally `http://localhost:5173`).

## Architecture

Every analysis concern lives in its own Java file under `backend/src/main/java/com/codeinsight/analyzer`:

- `TimeComplexityAnalyzer.java` — structural Big-O estimate based on max loop depth.
- `SpaceComplexityAnalyzer.java` — auxiliary allocation estimate.
- `NestedLoopAnalyzer.java` — nested loop locations and advice.
- `LongMethodAnalyzer.java` — methods over 35 lines.
- `EmptyCatchAnalyzer.java` — swallowed exceptions.
- `PotentialErrorAnalyzer.java` — common null-sensitive method calls.

The API accepts pasted code at `POST /api/analyze` and a single `.java` upload at `POST /api/analyze/file`.
