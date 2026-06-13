package com.aliniaz.tickettriage.ticket.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void getAnalysisReturnsPersistedAnalysis() throws Exception {
        String requestBody = """
            {
              "subject": "Cannot login",
              "body": "I cannot access my account after password reset.",
              "customerId": "CUST-1001"
            }
            """;

        String responseBody = mockMvc.perform(post("/api/tickets/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long analysisId = objectMapper.readTree(responseBody)
                .get("analysisId")
                .asLong();

        mockMvc.perform(get("/api/tickets/analyses/{analysisId}", analysisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analysisId").value(analysisId))
                .andExpect(jsonPath("$.subject").value("Cannot login"))
                .andExpect(jsonPath("$.body").value("I cannot access my account after password reset."))
                .andExpect(jsonPath("$.customerId").value("CUST-1001"))
                .andExpect(jsonPath("$.analysisSource").value("deterministic-stub"))
                .andExpect(jsonPath("$.rawModelOutput").isEmpty())
                .andExpect(jsonPath("$.status").value("VALIDATED"))
                .andExpect(jsonPath("$.status").value("VALIDATED"))
                .andExpect(jsonPath("$.category").value("ACCOUNT_ACCESS"))
                .andExpect(jsonPath("$.priority").value("URGENT"));
    }

    @Test
    void getAnalysisReturnsNotFoundForUnknownId() throws Exception {
        mockMvc.perform(get("/api/tickets/analyses/{analysisId}", 999999L))
                .andExpect(status().isNotFound());
    }
}