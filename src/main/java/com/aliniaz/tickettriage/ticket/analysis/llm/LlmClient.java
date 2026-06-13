package com.aliniaz.tickettriage.ticket.analysis.llm;

public interface LlmClient {

    String generate(String prompt);
}