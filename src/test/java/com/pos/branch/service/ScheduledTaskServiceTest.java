package com.pos.branch.service;

import com.pos.branch.model.Sale;
import com.pos.branch.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScheduledTaskServiceTest {

    private ScheduledTaskService scheduledTaskService;

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private SyncService syncService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduledTaskService = new ScheduledTaskService(saleRepository, syncService);
    }

    @Test
    void dailySyncToCentral_WithUnsyncedSales() {
        Sale sale = new Sale();
        sale.setSynced(false);
        List<Sale> unsynced = Collections.singletonList(sale);

        when(saleRepository.findBySyncedFalse()).thenReturn(unsynced);

        scheduledTaskService.dailySyncToCentral();

        assertTrue(sale.getSynced());
        verify(saleRepository).save(sale);
    }

    @Test
    void dailySyncToCentral_Empty() {
        when(saleRepository.findBySyncedFalse()).thenReturn(Collections.emptyList());

        scheduledTaskService.dailySyncToCentral();

        verify(saleRepository, never()).save(any());
    }

    @Test
    void archiveOldData() {
        scheduledTaskService.archiveOldData();
        verify(saleRepository).deleteByCreatedAtBefore(any(LocalDateTime.class));
    }

    private void assertTrue(boolean condition) {
        if (!condition) throw new AssertionError("Condition is false");
    }
}
