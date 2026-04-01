package com.pos.branch.service;

import com.pos.branch.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface ProductService {
    ProductResponse getProductByBarcode(String barcode);
    Page<ProductResponse> searchProducts(String query, String category, Pageable pageable);
    void updateProductPrice(Integer productId, BigDecimal newPrice);
}
