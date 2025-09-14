package com.esp.poller.model;

/**
 * Context for an event task. Models base for DP and EP tasks.
 */
public interface EventTaskContext {
    EventSim eventSim();
    default String getAssetId() {
        return null;
    }
    default String getRuleId() {
        return null;
    }
    Result getResult();
    void setResult(Result result);

    enum Result {
        SUCCESS,
        FAILURE_RETRYABLE,
        FAILURE_NON_RETRYABLE
    }
}
