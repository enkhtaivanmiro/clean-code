package com.pos.branch.controller;

import com.pos.branch.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/central")
@Tag(name = "Central Sync", description = "Endpoints for Central Server to Branch Server communication")
public class CentralSyncController {

    private final ProductService productService;

    public CentralSyncController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/price-update")
    @Operation(summary = "Push price update from Central", description = "Received from Central Server to update master prices in this branch")
    public ResponseEntity<String> updateMasterPrice(@RequestBody Map<String, Object> payload) {
        Object productIdObj = payload.get("productId");
        Object priceObj = payload.get("newPrice");
        
        if (productIdObj == null || priceObj == null) {
            return ResponseEntity.badRequest().body("productId and newPrice are required");
        }

        Integer productId = Integer.valueOf(productIdObj.toString());
        BigDecimal newPrice = new BigDecimal(priceObj.toString());
        
        productService.updateProductPrice(productId, newPrice);
        
        return ResponseEntity.ok("Price updated and observers notified.");
    }
}
