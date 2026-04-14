package com.pos.branch.service;

import com.pos.branch.model.Sale;
import com.pos.branch.repository.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);
    private final SaleRepository saleRepository;
    private final SyncService syncService;

    public ScheduledTaskService(SaleRepository saleRepository, SyncService syncService) {
        this.saleRepository = saleRepository;
        this.syncService = syncService;
    }

    /**
     * Daily synchronization of unsynced sales records to the Central Server.
     * Scheduled for 10:00 PM every day.
     */
    @Scheduled(cron = "0 0 22 * * *")
    @Transactional
    public void dailySyncToCentral() {
        logger.info("Starting scheduled daily sync to Central Server at 10 PM...");
        List<Sale> unsyncedSales = saleRepository.findBySyncedFalse();
        
        if (unsyncedSales.isEmpty()) {
            logger.info("No unsynced sales found for daily sync.");
            return;
        }

        logger.info("Found {} unsynced sales. Initiating sync...", unsyncedSales.size());
        // In a real scenario, we would map these to a SyncRequest and call a remote API.
        // For this homework, we mark them as synced to simulate the process.
        for (Sale sale : unsyncedSales) {
            sale.setSynced(true);
            saleRepository.save(sale);
        }
        logger.info("Daily sync completed successfully.");
    }

    /**
     * Data retention cleanup.
     * Keeps the last 1 year of data in the Branch Server.
     * Scheduled to run once a day at 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void archiveOldData() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        logger.info("Starting scheduled data cleanup. Deleting records older than: {}", oneYearAgo);
        
        try {
            saleRepository.deleteByCreatedAtBefore(oneYearAgo);
            logger.info("Data cleanup completed successfully.");
        } catch (Exception e) {
            logger.error("Error during data cleanup: {}", e.getMessage());
        }
    }
}
