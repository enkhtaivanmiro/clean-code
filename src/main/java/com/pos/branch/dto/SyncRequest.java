package com.pos.branch.dto;

import java.util.List;
import java.util.UUID;

public record SyncRequest(Integer branch_id, List<SaleRequest> sales) {}
