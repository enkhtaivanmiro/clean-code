package com.pos.branch.controller;

import com.pos.branch.dto.ProductResponse;
import com.pos.branch.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "Product Management Endpoints")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{barcode}")
    @Operation(summary = "Get product by barcode", description = "Looks up a product using its unique barcode string")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponse> getProductByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(productService.getProductByBarcode(barcode));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name and optionally filter by category")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.searchProducts(query, category, PageRequest.of(page, size)));
    }

    @PutMapping("/{id}/price")
    @Operation(summary = "Update product price", description = "Updates a product's price and notifies observers")
    public ResponseEntity<Void> updatePrice(@PathVariable Integer id, @RequestParam BigDecimal price) {
        productService.updateProductPrice(id, price);
        return ResponseEntity.noContent().build();
    }
}
