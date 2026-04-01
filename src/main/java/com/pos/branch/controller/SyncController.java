package com.pos.branch.controller;

import com.pos.branch.dto.SyncRequest;
import com.pos.branch.dto.SyncResponse;
import com.pos.branch.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/sync")
@Tag(name = "Sync", description = "Branch Data Synchronization Endpoints")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping
    @Operation(summary = "Sync branch sales", description = "Asynchronously pushes unsynced sales data to the central server (simulated)")
    public CompletableFuture<ResponseEntity<SyncResponse>> syncSales(@RequestBody SyncRequest request) {
        return syncService.syncSales(request)
                .thenApply(ResponseEntity::ok);
    }
}
