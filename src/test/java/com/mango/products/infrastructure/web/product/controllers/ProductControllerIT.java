package com.mango.products.infrastructure.web.product.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mango.products.infrastructure.TestDatabaseInitializer;
import com.mango.products.infrastructure.web.product.contracts.CreatePriceRequest;
import com.mango.products.infrastructure.web.product.contracts.CreateProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for ProductController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(value = TestDatabaseInitializer.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        String name = unique("Zapatillas");

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(productRequest(name, "Modelo 2025 edición limitada"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.description", is("Modelo 2025 edición limitada")));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsEmpty() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(productRequest("", "Descripción"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateAndPreventDuplicateProduct() throws Exception {
        String name = unique("Duplicate");

        createProduct(name);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(productRequest(name, "Producto"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("DUPLICATE_PRODUCT_NAME")));
    }

    @Test
    void shouldAddPriceSuccessfully() throws Exception {
        Long productId = createProduct(unique("Price"));

        addPrice(productId,
                BigDecimal.valueOf(99.99),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30));

        mockMvc.perform(get("/products/{id}/prices", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prices", hasSize(1)))
                .andExpect(jsonPath("$.prices[0].value", is(99.99)));
    }

    @Test
    void shouldRejectOverlappingPrices() throws Exception {
        Long productId = createProduct(unique("Overlap"));

        addPrice(productId,
                BigDecimal.valueOf(99.99),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30));

        mockMvc.perform(post("/products/{id}/prices", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(priceRequest(
                                BigDecimal.valueOf(150),
                                LocalDate.of(2024, 4, 1),
                                LocalDate.of(2024, 8, 31)))))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGetCurrentPriceSuccessfully() throws Exception {
        Long productId = createProduct(unique("Current"));

        addPrice(productId,
                BigDecimal.valueOf(99.99),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30));

        mockMvc.perform(get("/products/{id}/prices", productId)
                        .param("date", "2024-04-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is(99.99)));
    }

    @Test
    void shouldReturnPriceHistory() throws Exception {
        Long productId = createProduct(unique("History"));

        addPrice(productId,
                BigDecimal.valueOf(99.99),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30));

        addPrice(productId,
                BigDecimal.valueOf(199.99),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 6, 30));

        mockMvc.perform(get("/products/{id}/prices", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prices", hasSize(2)));
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/products/{id}/prices", 99999L))
                .andExpect(status().isNotFound());
    }

    private String unique(String base) {
        return base + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private CreateProductRequest productRequest(String name, String desc) {
        return new CreateProductRequest(name, desc);
    }

    private CreatePriceRequest priceRequest(BigDecimal value, LocalDate start, LocalDate end) {
        return new CreatePriceRequest(value, start, end);
    }

    private Long createProduct(String name) throws Exception {
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(productRequest(name, "desc"))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }

    private void addPrice(Long productId, BigDecimal value, LocalDate start, LocalDate end) throws Exception {
        mockMvc.perform(post("/products/{id}/prices", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(priceRequest(value, start, end))))
                .andExpect(status().isOk());
    }

}
