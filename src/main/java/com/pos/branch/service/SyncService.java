package com.pos.branch.service;

import com.pos.branch.dto.SyncRequest;
import com.pos.branch.dto.SyncResponse;
import java.util.concurrent.CompletableFuture;

public interface SyncService {
    CompletableFuture<SyncResponse> syncSales(SyncRequest request);
}
