package dev.ordersync.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired MockMvc mvc;

    @Test
    void acceptsValidOrder() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"orderId":"ORD-101","customerName":"John","amount":150.00,"currency":"EUR"}
                        """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.reference").value("ORD-101"))
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void rejectsInvalidOrder() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"orderId":"","customerName":"John","amount":-1,"currency":"EUR"}
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                .content("{not-json"))
                .andExpect(status().isBadRequest());
    }
}
