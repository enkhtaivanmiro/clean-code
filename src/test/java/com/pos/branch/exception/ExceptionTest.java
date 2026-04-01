package com.pos.branch.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testProductNotFoundException() {
        ProductNotFoundException ex = new ProductNotFoundException("Not found");
        ResponseEntity<Map<String, Object>> response = handler.handleProductNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().get("error"));
    }

    @Test
    void testInsufficientStockException() {
        InsufficientStockException ex = new InsufficientStockException("Low stock");
        ResponseEntity<Map<String, Object>> response = handler.handleInsufficientStock(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Low stock", response.getBody().get("error"));
    }

    @Test
    void testDuplicateSaleException() {
        DuplicateSaleException ex = new DuplicateSaleException("Duplicate");
        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateSale(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate", response.getBody().get("error"));
    }
}
