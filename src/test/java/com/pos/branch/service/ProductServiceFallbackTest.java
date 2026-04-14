package com.pos.branch.service;

import com.pos.branch.dto.ProductResponse;
import com.pos.branch.exception.ProductNotFoundException;
import com.pos.branch.model.Barcode;
import com.pos.branch.model.Category;
import com.pos.branch.model.Product;
import com.pos.branch.repository.BarcodeRepository;
import com.pos.branch.repository.ProductPriceRepository;
import com.pos.branch.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProductServiceFallbackTest {

    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private BarcodeRepository barcodeRepository;
    @Mock
    private ProductPriceRepository productPriceRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl(productRepository, barcodeRepository, productPriceRepository, eventPublisher);
    }

    @Test
    void getProductByBarcode_Success_DirectBarcode() {
        String code = "12345";
        Product product = new Product();
        product.setId(1);
        product.setName("Test Product");
        Category category = new Category();
        category.setName("Electronics");
        product.setCategory(category);
        
        Barcode barcode = new Barcode();
        barcode.setCode(code);
        barcode.setProduct(product);
        product.setBarcodes(java.util.Collections.singletonList(barcode));

        when(barcodeRepository.findByCode(code)).thenReturn(Optional.of(barcode));
        when(productPriceRepository.findLatestByProductId(1)).thenReturn(Optional.empty());

        ProductResponse response = productService.getProductByBarcode(code);

        assertEquals("Test Product", response.name());
        assertEquals(code, response.barcode());
        verify(barcodeRepository).findByCode(code);
    }

    @Test
    void getProductByBarcode_FallbackToName() {
        String query = "OrphanProduct";
        Product product = new Product();
        product.setId(2);
        product.setName("OrphanProduct");
        Category category = new Category();
        category.setName("Food");
        product.setCategory(category);
        product.setBarcodes(java.util.Collections.emptyList());

        when(barcodeRepository.findByCode(query)).thenReturn(Optional.empty());
        when(productRepository.findFirstByNameContainingIgnoreCase(query)).thenReturn(Optional.of(product));
        when(productPriceRepository.findLatestByProductId(2)).thenReturn(Optional.empty());

        ProductResponse response = productService.getProductByBarcode(query);

        assertEquals("OrphanProduct", response.name());
        verify(productRepository).findFirstByNameContainingIgnoreCase(query);
    }

    @Test
    void getProductByBarcode_FallbackToCategory() {
        String query = "Fruit";
        Product product = new Product();
        product.setId(3);
        product.setName("Apple");
        Category category = new Category();
        category.setName("Fruit");
        product.setCategory(category);
        product.setBarcodes(java.util.Collections.emptyList());

        when(barcodeRepository.findByCode(query)).thenReturn(Optional.empty());
        when(productRepository.findFirstByNameContainingIgnoreCase(query)).thenReturn(Optional.empty());
        when(productRepository.findFirstByCategoryName(query)).thenReturn(Optional.of(product));
        when(productPriceRepository.findLatestByProductId(3)).thenReturn(Optional.empty());

        ProductResponse response = productService.getProductByBarcode(query);

        assertEquals("Apple", response.name());
        assertEquals("Fruit", response.category());
        verify(productRepository).findFirstByCategoryName(query);
    }

    @Test
    void getProductByBarcode_NotFound_ThrowsException() {
        String query = "Unknown";
        when(barcodeRepository.findByCode(query)).thenReturn(Optional.empty());
        when(productRepository.findFirstByNameContainingIgnoreCase(query)).thenReturn(Optional.empty());
        when(productRepository.findFirstByCategoryName(query)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductByBarcode(query));
    }
}
