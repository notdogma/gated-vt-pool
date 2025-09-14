package com.esp.poller.model;

import java.util.List;

/**
 * Simulated event, just need the asset IDs for this.
 */
public record EventSim(String eventId, List<String> assetIds) {
}
