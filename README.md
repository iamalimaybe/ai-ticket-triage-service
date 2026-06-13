# AI Ticket Triage Service

A Java/Spring Boot AI backend for structured support ticket triage with validated LLM output.

## Project Goal

This project demonstrates how traditional backend engineering can be combined with practical AI workflow design.

The service will accept support tickets, use an LLM to produce structured triage output, validate the output, persist the result, and expose the analysis through an API.

## Planned MVP Scope

- Submit a support ticket through an API
- Classify the ticket category
- Assign ticket priority
- Extract customer intent
- Detect missing information
- Store raw model output and validated output
- Return a structured triage result
- Add deterministic validation and regression-style tests
- Add a lightweight review UI later

## Tech Stack

- Java 17
- Spring Boot
- PostgreSQL
- Docker Compose
- Ollama or API-compatible LLM provider
- React UI later

## Portfolio Focus

This project is intended to show production-aware AI integration, not a basic chatbot.

The focus areas are:

- Backend API design
- Structured LLM output
- Validation-first AI handling
- Persistence and auditability
- Testable behavior
- Clear failure handling