package com.pos.branch.service;

import com.pos.branch.dto.SaleItemRequest;
import com.pos.branch.dto.SaleRequest;
import com.pos.branch.dto.SaleResponse;
import com.pos.branch.model.*;
import com.pos.branch.pattern.strategy.PaymentStrategy;
import com.pos.branch.pattern.strategy.PaymentStrategyFactory;
import com.pos.branch.repository.*;
import com.pos.branch.event.SaleCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock private SaleRepository saleRepository;
    @Mock private SaleItemRepository saleItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private BranchRepository branchRepository;
    @Mock private POSTerminalRepository posTerminalRepository;
    @Mock private PaymentTypeRepository paymentTypeRepository;
    @Mock private DiscountRuleRepository discountRuleRepository;
    @Mock private PaymentStrategyFactory paymentStrategyFactory;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SaleServiceImpl saleService;

    private SaleRequest saleRequest;
    private UUID saleId;

    @BeforeEach
    void setUp() {
        saleId = UUID.randomUUID();
        saleRequest = new SaleRequest(
                saleId, 1, 1, 1,
                List.of(new SaleItemRequest(1, 2, new BigDecimal("1500.00"), null, BigDecimal.ZERO)),
                new BigDecimal("3000.00")
        );
        ReflectionTestUtils.setField(saleService, "self", saleService);
    }

    @Test
    void testCreateSale_New_Success() {
        when(saleRepository.findByUuid(saleId)).thenReturn(Optional.empty());
        when(branchRepository.findById(1)).thenReturn(Optional.of(new Branch()));
        when(posTerminalRepository.findById(1)).thenReturn(Optional.of(new POSTerminal()));
        
        PaymentType py = new PaymentType();
        py.setName("CASH");
        when(paymentTypeRepository.findById(1)).thenReturn(Optional.of(py));

        Product product = new Product();
        Category cat = new Category();
        cat.setName("Cat1");
        product.setCategory(cat);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        PaymentStrategy strategy = mock(PaymentStrategy.class);
        when(paymentStrategyFactory.getStrategy("CASH")).thenReturn(strategy);

        SaleResponse response = saleService.createSale(saleRequest);

        assertNotNull(response);
        assertEquals(saleId, response.id());
        assertEquals("created", response.status());
        verify(saleRepository, times(1)).save(any(Sale.class));
        verify(eventPublisher, times(1)).publishEvent(any(SaleCreatedEvent.class));
    }

    @Test
    void testCreateSale_Idempotent_ReturnsExisting() {
        when(saleRepository.findByUuid(saleId)).thenReturn(Optional.of(new Sale()));

        SaleResponse response = saleService.createSale(saleRequest);

        assertEquals(saleId, response.id());
        assertEquals("already_exists", response.status());
        verify(saleRepository, never()).save(any(Sale.class));
    }
}
