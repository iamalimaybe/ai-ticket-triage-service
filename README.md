# AI Ticket Triage Service

Java/Spring Boot AI backend for structured support ticket triage with validated LLM output, persistence, auditability, and review decisioning.

## Project Goal

This project demonstrates how traditional backend engineering can be combined with practical AI workflow design.

The service accepts support tickets, analyzes them using either a deterministic analyzer or a local Ollama model, validates the structured analysis result, persists both raw and validated output, and exposes APIs for retrieval and review workflows.

## Why This Project Exists

LLM output should not be trusted just because it looks correct.

This project treats AI output as untrusted until it passes controlled parsing, validation, persistence, and review decision rules.

The focus is not building a chatbot. The focus is building a production-aware backend service around AI output.

## Current Features

* Submit a support ticket for analysis
* Deterministic analyzer for stable local development and tests
* Switchable Ollama analyzer for local LLM-based analysis
* Prompt contract for structured ticket triage output
* Raw model output parsing
* Analyzer output validation
* PostgreSQL persistence
* Liquibase SQL migrations
* Stored raw model output for audit/debugging
* Stored parsed analysis result
* Stored model confidence
* Created and updated audit timestamps
* Property-driven human review decision threshold
* List endpoint for review queues
* Detail endpoint for inspecting saved analysis
* Controller, parser, validator, service, and review decision tests

## Tech Stack

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* PostgreSQL
* Liquibase
* Docker Compose
* Ollama
* Qwen3 local model
* JUnit 5
* MockMvc
* Mockito
* Lombok

## API Endpoints

### Analyze Ticket

`POST /api/tickets/analyze`

Example request:

```json
{
  "subject": "Cannot login",
  "body": "I cannot access my account after password reset.",
  "customerId": "CUST-1001"
}
```

Example response:

```json
{
  "analysisId": 4,
  "analysisSource": "llm-json-parser",
  "modelConfidence": 0.95,
  "reviewStatus": "NOT_REQUIRED",
  "reviewReason": null,
  "status": "VALIDATED",
  "category": "ACCOUNT_ACCESS",
  "priority": "HIGH",
  "customerIntent": "Customer needs to log in after password reset.",
  "missingInformation": [],
  "validationMessages": [
    "Model output parsed successfully."
  ]
}
```

### Get Analysis Detail

`GET /api/tickets/analyses/{analysisId}`

Returns the original ticket, raw model output, parsed result, validation status, review status, and audit timestamps.

### List Analyses

`GET /api/tickets/analyses`

Optional filters:

```text
reviewStatus=NEEDS_REVIEW
page=0
size=10
```

Example:

`GET /api/tickets/analyses?reviewStatus=NEEDS_REVIEW&page=0&size=10`

## Analyzer Modes

The service supports two analyzer modes.

### Deterministic Mode

Default mode.

```properties
TICKET_TRIAGE_ANALYZER_MODE=deterministic
```

Used for stable tests and predictable local development.

### Ollama Mode

Uses a local Ollama model.

```properties
TICKET_TRIAGE_ANALYZER_MODE=ollama
OLLAMA_MODEL=qwen3:4b
```

The Ollama response is parsed, validated, persisted, and assigned a review decision.

## Review Decision Logic

Review decisioning is property-driven.

```properties
TICKET_TRIAGE_REVIEW_CONFIDENCE_THRESHOLD=0.70
```

Current rules:

* Failed analysis requires review
* LLM confidence below threshold requires review
* Valid deterministic analysis does not require review
* Valid LLM analysis at or above threshold does not require review

## Local Setup

Start PostgreSQL:

```powershell
docker compose up -d
```

Run tests:

```powershell
.\mvnw clean test
```

Run the app:

```powershell
.\mvnw spring-boot:run
```

Health check:

```powershell
curl http://localhost:8080/actuator/health
```

## Run With Ollama

Make sure Ollama is running and the model is available locally.

Example:

```powershell
ollama pull qwen3:4b
```

Set environment variables:

```powershell
$env:TICKET_TRIAGE_ANALYZER_MODE="ollama"
$env:OLLAMA_MODEL="qwen3:4b"
```

Run:

```powershell
.\mvnw spring-boot:run
```

Submit a ticket:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/api/tickets/analyze" `
  -ContentType "application/json" `
  -Body '{"subject":"Cannot login","body":"I cannot access my account after password reset.","customerId":"CUST-1001"}' |
  ConvertTo-Json -Depth 10
```

## Portfolio Focus

This project demonstrates:

* Java/Spring Boot backend engineering
* AI integration through a local LLM runtime
* Structured LLM output handling
* Validation-first AI workflow design
* Persistence of raw and validated AI output
* Review decisioning for uncertain AI results
* Testable backend behavior around LLM output

The project is intentionally backend-first. A lightweight review UI can be added later as a thin layer over the existing API.
