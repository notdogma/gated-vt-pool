package com.esp.poller.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Context for a DP task. This has the event, the assetId and the ruleId that are sent to DP for processing.
 * It also has a result field that will be set base on the result of the DP call.
 */
@ToString
public class EventTaskDPContext implements EventTaskContext {
    private final EventSim eventSim;
    private final String assetId;
    private final String ruleId;
    @Getter
    @Setter
    private Result result;

    public EventTaskDPContext( EventSim eventSim, String assetId, String ruleId ) {
        this.eventSim = eventSim;
        this.assetId = assetId;
        this.ruleId = ruleId;
    }

    public EventTaskDPContext( EventSim eventSim, String assetId, String ruleId, Result result ) {
        this.eventSim = eventSim;
        this.assetId = assetId;
        this.ruleId = ruleId;
        this.result = result;
    }

    @Override
    public EventSim eventSim() {
        return eventSim;
    }
}
