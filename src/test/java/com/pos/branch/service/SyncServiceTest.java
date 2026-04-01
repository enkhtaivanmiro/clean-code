package com.pos.branch.service;

import com.pos.branch.dto.SaleRequest;
import com.pos.branch.dto.SyncRequest;
import com.pos.branch.dto.SyncResponse;
import com.pos.branch.model.Sale;
import com.pos.branch.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SyncServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SyncServiceImpl syncService;

    @Test
    void testSyncSales_Success() throws Exception {
        UUID saleId = UUID.randomUUID();
        Sale sale = new Sale();
        sale.setId(saleId);
        sale.setSynced(false);

        SaleRequest saleReq = new SaleRequest(saleId, 1, 1, 1, List.of(), java.math.BigDecimal.ZERO);
        SyncRequest syncRequest = new SyncRequest(1, List.of(saleReq));

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));

        CompletableFuture<SyncResponse> future = syncService.syncSales(syncRequest);
        SyncResponse response = future.get();

        assertEquals(1, response.synced_count());
        assertTrue(sale.getSynced());
        verify(saleRepository, times(1)).save(sale);
    }
}
