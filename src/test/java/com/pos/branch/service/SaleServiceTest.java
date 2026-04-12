package com.pos.branch.service;

import com.pos.branch.dto.SaleItemRequest;
import com.pos.branch.dto.SaleRequest;
import com.pos.branch.dto.SaleResponse;
import com.pos.branch.model.*;
import com.pos.branch.pattern.strategy.PaymentStrategy;
import com.pos.branch.pattern.strategy.PaymentStrategyFactory;
import com.pos.branch.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    @Mock private InventoryRepository inventoryRepository;
    @Mock private BranchRepository branchRepository;
    @Mock private POSTerminalRepository posTerminalRepository;
    @Mock private PaymentTypeRepository paymentTypeRepository;
    @Mock private DiscountRuleRepository discountRuleRepository;
    @Mock private PaymentStrategyFactory paymentStrategyFactory;

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
    }

    @Test
    void testCreateSale_New_Success() {
        when(saleRepository.findById(saleId)).thenReturn(Optional.empty());
        when(branchRepository.findById(1)).thenReturn(Optional.of(new Branch()));
        when(posTerminalRepository.findById(1)).thenReturn(Optional.of(new POSTerminal()));
        
        PaymentType py = new PaymentType();
        py.setName("CASH");
        when(paymentTypeRepository.findById(1)).thenReturn(Optional.of(py));

        Inventory inventory = new Inventory();
        inventory.setQuantity(10);
        when(inventoryRepository.findWithLock(any(), any())).thenReturn(Optional.of(inventory));
        when(productRepository.findById(1)).thenReturn(Optional.of(new Product()));

        PaymentStrategy strategy = mock(PaymentStrategy.class);
        when(paymentStrategyFactory.getStrategy("CASH")).thenReturn(strategy);

        SaleResponse response = saleService.createSale(saleRequest);

        assertNotNull(response);
        assertEquals(saleId, response.id());
        assertEquals("stored", response.status());
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    void testCreateSale_Idempotent_ReturnsExisting() {
        when(saleRepository.findById(saleId)).thenReturn(Optional.of(new Sale()));

        SaleResponse response = saleService.createSale(saleRequest);

        assertEquals(saleId, response.id());
        assertEquals("stored", response.status());
        verify(saleRepository, never()).save(any(Sale.class));
    }
}
