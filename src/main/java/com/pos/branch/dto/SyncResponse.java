package com.pos.branch.dto;

import java.util.List;
import java.util.UUID;

public record SyncResponse(int synced_count, List<UUID> failed_ids) {}
