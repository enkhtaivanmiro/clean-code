package com.pos.branch.service;

import com.pos.branch.dto.ProductResponse;
import com.pos.branch.event.PriceUpdatedEvent;
import com.pos.branch.exception.ProductNotFoundException;
import com.pos.branch.model.Barcode;
import com.pos.branch.model.Product;
import com.pos.branch.model.ProductPrice;
import com.pos.branch.repository.BarcodeRepository;
import com.pos.branch.repository.ProductPriceRepository;
import com.pos.branch.repository.ProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BarcodeRepository barcodeRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProductServiceImpl(ProductRepository productRepository, 
                              BarcodeRepository barcodeRepository, 
                              ProductPriceRepository productPriceRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.barcodeRepository = barcodeRepository;
        this.productPriceRepository = productPriceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ProductResponse getProductByBarcode(String barcodeCode) {
        // 1. Try barcode lookup
        Product product = barcodeRepository.findByCode(barcodeCode)
                .map(Barcode::getProduct)
                .orElse(null);

        // 2. Fallback to name search
        if (product == null) {
            product = productRepository.findFirstByNameContainingIgnoreCase(barcodeCode).orElse(null);
        }

        // 3. Fallback to category search
        if (product == null) {
            product = productRepository.findFirstByCategoryName(barcodeCode).orElse(null);
        }

        if (product == null) {
            throw new ProductNotFoundException("Product not found for barcode or search criteria: " + barcodeCode);
        }

        BigDecimal price = getLatestPrice(product.getId());
        String actualBarcode = product.getBarcodes().isEmpty() ? barcodeCode : product.getBarcodes().get(0).getCode();
        return mapToResponse(product, actualBarcode, price);
    }

    @Override
    public Page<ProductResponse> searchProducts(String query, String category, Pageable pageable) {
        return productRepository.searchProducts(query, category, pageable)
                .map(p -> {
                    String barcode = p.getBarcodes().isEmpty() ? null : p.getBarcodes().get(0).getCode();
                    return mapToResponse(p, barcode, getLatestPrice(p.getId()));
                });
    }

    @Override
    @Transactional
    public void updateProductPrice(Integer productId, BigDecimal newPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        
        ProductPrice productPrice = new ProductPrice();
        productPrice.setProduct(product);
        productPrice.setPrice(newPrice);
        productPrice.setUpdatedAt(LocalDateTime.now());
        productPriceRepository.save(productPrice);
        
        // Emit Spring Event (Observer Pattern)
        eventPublisher.publishEvent(new PriceUpdatedEvent(this, productId, newPrice));
    }

    @Override
    public List<ProductResponse> getUpdatedProducts(LocalDateTime since) {
        return productPriceRepository.findByUpdatedAtAfter(since).stream()
                .map(pp -> {
                    Product p = pp.getProduct();
                    String barcode = p.getBarcodes().isEmpty() ? null : p.getBarcodes().get(0).getCode();
                    return mapToResponse(p, barcode, pp.getPrice());
                })
                .collect(Collectors.toList());
    }

    private BigDecimal getLatestPrice(Integer productId) {
        return productPriceRepository.findLatestByProductId(productId)
                .map(ProductPrice::getPrice)
                .orElse(BigDecimal.ZERO);
    }

    private ProductResponse mapToResponse(Product p, String barcode, BigDecimal price) {
        return new ProductResponse(p.getId(), p.getName(), barcode, price, p.getCategory().getName());
    }
}
