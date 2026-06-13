package com.aliniaz.tickettriage.ticket.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void analyzeReturnsValidatedAccountAccessResult() throws Exception {
        String requestBody = """
                {
                  "subject": "Cannot login",
                  "body": "I cannot access my account after password reset.",
                  "customerId": "CUST-1001"
                }
                """;

        mockMvc.perform(post("/api/tickets/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analysisId", notNullValue()))
                .andExpect(jsonPath("$.analysisSource").value("deterministic-stub"))
                .andExpect(jsonPath("$.status").value("VALIDATED"))
                .andExpect(jsonPath("$.category").value("ACCOUNT_ACCESS"))
                .andExpect(jsonPath("$.priority").value("URGENT"))
                .andExpect(jsonPath("$.customerIntent").value("Customer needs help accessing their account."))
                .andExpect(jsonPath("$.missingInformation").isArray())
                .andExpect(jsonPath("$.validationMessages").isArray())
                .andExpect(jsonPath("$.validationMessages", hasItem("Request passed API validation.")));
    }

    @Test
    void analyzeRejectsBlankSubject() throws Exception {
        String requestBody = """
                {
                  "subject": "",
                  "body": "I cannot access my account after password reset.",
                  "customerId": "CUST-1001"
                }
                """;

        mockMvc.perform(post("/api/tickets/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void analyzeDetectsMissingTechnicalIssueInformation() throws Exception {
        String requestBody = """
                {
                  "subject": "Application error",
                  "body": "The application is not working.",
                  "customerId": ""
                }
                """;

        mockMvc.perform(post("/api/tickets/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analysisId", notNullValue()))
                .andExpect(jsonPath("$.status").value("VALIDATED"))
                .andExpect(jsonPath("$.category").value("TECHNICAL_ISSUE"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.missingInformation", hasItem("customerId")))
                .andExpect(jsonPath("$.missingInformation", hasItem("stepsToReproduce")))
                .andExpect(jsonPath("$.missingInformation", hasItem("errorMessageOrScreenshot")));
    }
}