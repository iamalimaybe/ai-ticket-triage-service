package com.aliniaz.tickettriage.ticket.analysis.llm.impl;

import com.aliniaz.tickettriage.ticket.analysis.llm.config.OllamaProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OllamaLlmClientTest {

    @Test
    void generateReturnsRawModelResponse() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("http://localhost:11434");

        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        OllamaProperties properties = new OllamaProperties();
        properties.setBaseUrl("http://localhost:11434");
        properties.setModel("qwen3:4b");

        OllamaLlmClient client = new OllamaLlmClient(builder.build(), properties);

        server.expect(requestTo("http://localhost:11434/api/generate"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("""
                        {
                          "response": "{\\"category\\":\\"ACCOUNT_ACCESS\\"}",
                          "done": true
                        }
                        """, MediaType.APPLICATION_JSON));

        String response = client.generate("Analyze this ticket.");

        assertThat(response).isEqualTo("{\"category\":\"ACCOUNT_ACCESS\"}");

        server.verify();
    }

    @Test
    void generateThrowsWhenOllamaReturnsEmptyResponse() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("http://localhost:11434");

        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        OllamaProperties properties = new OllamaProperties();
        properties.setBaseUrl("http://localhost:11434");
        properties.setModel("qwen3:4b");

        OllamaLlmClient client = new OllamaLlmClient(builder.build(), properties);

        server.expect(requestTo("http://localhost:11434/api/generate"))
                .andRespond(withSuccess("""
                        {
                          "response": "",
                          "done": true
                        }
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.generate("Analyze this ticket."))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ollama returned an empty response.");

        server.verify();
    }
}