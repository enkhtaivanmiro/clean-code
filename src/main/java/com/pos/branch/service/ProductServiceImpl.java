package com.pos.branch.service;

import com.pos.branch.dto.ProductResponse;
import com.pos.branch.exception.ProductNotFoundException;
import com.pos.branch.model.Barcode;
import com.pos.branch.model.Product;
import com.pos.branch.model.ProductPrice;
import com.pos.branch.pattern.observer.PriceChangeEvent;
import com.pos.branch.pattern.observer.PriceChangePublisher;
import com.pos.branch.repository.BarcodeRepository;
import com.pos.branch.repository.ProductPriceRepository;
import com.pos.branch.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BarcodeRepository barcodeRepository;
    private final ProductPriceRepository productPriceRepository;
    private final PriceChangePublisher priceChangePublisher;

    public ProductServiceImpl(ProductRepository productRepository, 
                              BarcodeRepository barcodeRepository, 
                              ProductPriceRepository productPriceRepository,
                              PriceChangePublisher priceChangePublisher) {
        this.productRepository = productRepository;
        this.barcodeRepository = barcodeRepository;
        this.productPriceRepository = productPriceRepository;
        this.priceChangePublisher = priceChangePublisher;
    }

    @Override
    public ProductResponse getProductByBarcode(String barcodeCode) {
        Barcode barcode = barcodeRepository.findByCode(barcodeCode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found for barcode: " + barcodeCode));
        Product product = barcode.getProduct();
        BigDecimal price = getLatestPrice(product.getId());
        return mapToResponse(product, barcodeCode, price);
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
        
        BigDecimal oldPrice = getLatestPrice(productId);
        
        ProductPrice productPrice = new ProductPrice();
        productPrice.setProduct(product);
        productPrice.setPrice(newPrice);
        productPrice.setUpdatedAt(LocalDateTime.now());
        productPriceRepository.save(productPrice);
        
        priceChangePublisher.notifyObservers(new PriceChangeEvent(productId, oldPrice, newPrice, LocalDateTime.now()));
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
