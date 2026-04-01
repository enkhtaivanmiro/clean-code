package com.pos.branch.service;

import com.pos.branch.dto.ProductResponse;
import com.pos.branch.exception.ProductNotFoundException;
import com.pos.branch.model.*;
import com.pos.branch.pattern.observer.PriceChangePublisher;
import com.pos.branch.repository.BarcodeRepository;
import com.pos.branch.repository.ProductPriceRepository;
import com.pos.branch.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private BarcodeRepository barcodeRepository;
    @Mock
    private ProductPriceRepository productPriceRepository;
    @Mock
    private PriceChangePublisher priceChangePublisher;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Barcode barcode;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Grocery");

        product = new Product();
        product.setId(1);
        product.setName("Milk");
        product.setCategory(category);

        barcode = new Barcode();
        barcode.setCode("1001");
        barcode.setProduct(product);
    }

    @Test
    void testGetProductByBarcode_Found() {
        when(barcodeRepository.findByCode("1001")).thenReturn(Optional.of(barcode));
        
        ProductPrice price = new ProductPrice();
        price.setPrice(new BigDecimal("3500.00"));
        when(productPriceRepository.findLatestByProductId(1)).thenReturn(Optional.of(price));

        ProductResponse response = productService.getProductByBarcode("1001");

        assertNotNull(response);
        assertEquals("Milk", response.name());
        assertEquals(new BigDecimal("3500.00"), response.price());
    }

    @Test
    void testGetProductByBarcode_NotFound() {
        when(barcodeRepository.findByCode(anyString())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductByBarcode("9999"));
    }
}
