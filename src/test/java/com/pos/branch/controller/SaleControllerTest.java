package com.pos.branch.controller;

import com.pos.branch.dto.SaleResponse;
import com.pos.branch.service.SaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaleController.class)
public class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaleService saleService;

    @Test
    void testCreateSale_StatusCreated() throws Exception {
        UUID saleId = UUID.randomUUID();
        when(saleService.createSale(any())).thenReturn(new SaleResponse(saleId, "stored"));

        String saleRequestJson = """
                {
                    "id": "%s",
                    "branch_id": 1,
                    "pos_id": 1,
                    "payment_type_id": 1,
                    "items": [
                        { "product_id": 1, "quantity": 1, "price": 100.0, "discount_amount": 0.0 }
                    ],
                    "total_amount": 100.0
                }
                """.formatted(saleId);

        mockMvc.perform(post("/api/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(saleRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saleId.toString()))
                .andExpect(jsonPath("$.status").value("stored"));
    }
}
