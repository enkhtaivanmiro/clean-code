package com.pos.branch.service;

import com.pos.branch.dto.SaleItemRequest;
import com.pos.branch.dto.SaleRequest;
import com.pos.branch.dto.SaleResponse;
import com.pos.branch.event.SaleCreatedEvent;
import com.pos.branch.exception.ProductNotFoundException;
import com.pos.branch.model.*;
import com.pos.branch.pattern.strategy.PaymentRequest;
import com.pos.branch.pattern.strategy.PaymentStrategy;
import com.pos.branch.pattern.strategy.PaymentStrategyFactory;
import com.pos.branch.repository.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SaleServiceImpl implements SaleService {
    private static final Map<UUID, Object> saleLocks = new ConcurrentHashMap<>();

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BranchRepository branchRepository;
    private final POSTerminalRepository posTerminalRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final DiscountRuleRepository discountRuleRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final ApplicationEventPublisher eventPublisher;

    @Lazy
    @Autowired
    private SaleServiceImpl self;

    public SaleServiceImpl(SaleRepository saleRepository,
                           SaleItemRepository saleItemRepository,
                           ProductRepository productRepository,
                           CategoryRepository categoryRepository,
                           BranchRepository branchRepository,
                           POSTerminalRepository posTerminalRepository,
                           PaymentTypeRepository paymentTypeRepository,
                           DiscountRuleRepository discountRuleRepository,
                           PaymentStrategyFactory paymentStrategyFactory,
                           ApplicationEventPublisher eventPublisher) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.branchRepository = branchRepository;
        this.posTerminalRepository = posTerminalRepository;
        this.paymentTypeRepository = paymentTypeRepository;
        this.discountRuleRepository = discountRuleRepository;
        this.paymentStrategyFactory = paymentStrategyFactory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public SaleResponse createSale(SaleRequest request) {
        // Synchronize on UUID to ensure thread-safe idempotency check for the same sale ID
        Object lock = saleLocks.computeIfAbsent(request.id(), k -> new Object());
        synchronized (lock) {
            try {
                return self.saveSaleTransactional(request);
            } finally {
                // Cleaning up the lock map to prevent memory leak
                saleLocks.remove(request.id());
            }
        }
    }

    @Transactional
    public SaleResponse saveSaleTransactional(SaleRequest request) {
        // IDEMPOTENCY check: if same UUID exists → return existing (NO duplicate insert)
        Optional<Sale> existingSale = saleRepository.findByUuid(request.id());
        if (existingSale.isPresent()) {
            return new SaleResponse(request.id(), "already_exists");
        }

        Branch branch = branchRepository.findById(request.branch_id())
                .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + request.branch_id()));

        POSTerminal pos = posTerminalRepository.findById(request.pos_id())
                .orElseThrow(() -> new IllegalArgumentException("POS not found: " + request.pos_id()));

        PaymentType paymentType = paymentTypeRepository.findById(request.payment_type_id())
                .orElseThrow(() -> new IllegalArgumentException("Payment type not found: " + request.payment_type_id()));

        Sale sale = new Sale();
        sale.setId(request.id());
        sale.setBranch(branch);
        sale.setPosTerminal(pos);
        sale.setPaymentType(paymentType);
        sale.setTotalAmount(request.total_amount());
        sale.setCreatedAt(LocalDateTime.now());
        sale.setSynced(false);

        List<SaleItem> items = request.items().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.product_id())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemReq.product_id()));

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProduct(product);
            item.setQuantity(itemReq.quantity());
            item.setPrice(itemReq.price());
            item.setDiscountAmount(itemReq.discount_amount());

            if (itemReq.discount_rule_id() != null) {
                DiscountRule rule = discountRuleRepository.findById(itemReq.discount_rule_id())
                        .orElseThrow(() -> new IllegalArgumentException("Rule not found: " + itemReq.discount_rule_id()));
                item.setDiscountRule(rule);
            }
            return item;
        }).toList();

        sale.setItems(items);
        saleRepository.save(sale);

        // Process payment via Strategy
        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(paymentType.getName());
        strategy.process(new PaymentRequest(request.total_amount(), "Sale ID: " + request.id()));

        // Emit SaleCreatedEvent (Observer Pattern)
        eventPublisher.publishEvent(new SaleCreatedEvent(this, sale));

        return new SaleResponse(sale.getId(), "created");
    }

    @Override
    public Sale getSaleById(UUID id) {
        return saleRepository.findByUuid(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found: " + id));
    }
}
