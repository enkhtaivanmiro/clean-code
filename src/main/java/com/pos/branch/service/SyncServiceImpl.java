package com.pos.branch.service;

import com.pos.branch.dto.SaleRequest;
import com.pos.branch.dto.SyncRequest;
import com.pos.branch.dto.SyncResponse;
import com.pos.branch.model.Sale;
import com.pos.branch.repository.SaleRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class SyncServiceImpl implements SyncService {

    private final SaleRepository saleRepository;

    public SyncServiceImpl(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    @Async("syncTaskExecutor")
    @Transactional
    public CompletableFuture<SyncResponse> syncSales(SyncRequest request) {
        List<UUID> failedIds = new ArrayList<>();
        int syncedCount = 0;

        for (SaleRequest saleReq : request.sales()) {
            UUID saleId = saleReq.id();
            try {
                Sale sale = saleRepository.findById(saleId).orElse(null);
                if (sale != null && !sale.getSynced()) {
                    sale.setSynced(true);
                    saleRepository.save(sale);
                    syncedCount++;
                }
            } catch (Exception e) {
                failedIds.add(saleId);
            }
        }

        return CompletableFuture.completedFuture(new SyncResponse(syncedCount, failedIds));
    }
}
