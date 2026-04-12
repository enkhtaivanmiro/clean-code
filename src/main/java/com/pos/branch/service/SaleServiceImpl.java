package com.pos.branch.service;

import com.pos.branch.dto.SaleItemRequest;
import com.pos.branch.dto.SaleRequest;
import com.pos.branch.dto.SaleResponse;
import com.pos.branch.exception.InsufficientStockException;
import com.pos.branch.exception.ProductNotFoundException;
import com.pos.branch.model.*;
import com.pos.branch.pattern.strategy.PaymentRequest;
import com.pos.branch.pattern.strategy.PaymentStrategy;
import com.pos.branch.pattern.strategy.PaymentStrategyFactory;
import com.pos.branch.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final POSTerminalRepository posTerminalRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final DiscountRuleRepository discountRuleRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;

    public SaleServiceImpl(SaleRepository saleRepository,
                           SaleItemRepository saleItemRepository,
                           ProductRepository productRepository,
                           InventoryRepository inventoryRepository,
                           BranchRepository branchRepository,
                           POSTerminalRepository posTerminalRepository,
                           PaymentTypeRepository paymentTypeRepository,
                           DiscountRuleRepository discountRuleRepository,
                           PaymentStrategyFactory paymentStrategyFactory) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.branchRepository = branchRepository;
        this.posTerminalRepository = posTerminalRepository;
        this.paymentTypeRepository = paymentTypeRepository;
        this.discountRuleRepository = discountRuleRepository;
        this.paymentStrategyFactory = paymentStrategyFactory;
    }

    @Override
    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        // IDEMPOTENCY check
        Optional<Sale> existingSale = saleRepository.findById(request.id());
        if (existingSale.isPresent()) {
            return new SaleResponse(request.id(), "stored");
        }

        Branch branch = branchRepository.findById(request.branch_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid branch ID"));
        POSTerminal pos = posTerminalRepository.findById(request.pos_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid POS ID"));
        PaymentType paymentType = paymentTypeRepository.findById(request.payment_type_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment type ID"));

        Sale sale = new Sale();
        sale.setId(request.id());
        sale.setBranch(branch);
        sale.setPosTerminal(pos);
        sale.setPaymentType(paymentType);
        sale.setTotalAmount(request.total_amount());
        sale.setCreatedAt(LocalDateTime.now());
        sale.setSynced(false);

        List<SaleItem> items = new ArrayList<>();
        for (SaleItemRequest itemReq : request.items()) {
            // Pessimistic inventory lock
            Inventory inventory = inventoryRepository.findWithLock(branch.getId(), itemReq.product_id())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found in this branch inventory: " + itemReq.product_id()));

            if (inventory.getQuantity() < itemReq.quantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + itemReq.product_id());
            }

            // Deduct stock
            inventory.setQuantity(inventory.getQuantity() - itemReq.quantity());
            inventoryRepository.save(inventory);

            Product product = productRepository.findById(itemReq.product_id())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + itemReq.product_id()));

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProduct(product);
            item.setQuantity(itemReq.quantity());
            item.setPrice(itemReq.price());
            item.setDiscountAmount(itemReq.discount_amount());

            if (itemReq.discount_rule_id() != null) {
                DiscountRule rule = discountRuleRepository.findById(itemReq.discount_rule_id())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid discount rule ID"));
                item.setDiscountRule(rule);
            }
            items.add(item);
        }

        sale.setItems(items);
        saleRepository.save(sale);

        // Process payment via Strategy
        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(paymentType.getName());
        strategy.process(new PaymentRequest(request.total_amount(), "Sale ID: " + request.id()));

        return new SaleResponse(sale.getId(), "stored");
    }

    @Override
    public Sale getSaleById(UUID id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found: " + id));
    }
}
