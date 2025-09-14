package com.esp.poller.ruleCache;

import com.esp.poller.model.EventSim;
import com.esp.poller.model.EventTaskDPContext;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Simulated rule cache. Takes an event and returns a stream of EventTaskDPContext objects containing the exploded set of assets and rules for that event
 * that need to be processed by DP.
 */
public class RuleCacheSim {
    public RuleCacheSim() {
    }

    public Stream<EventTaskDPContext> getRules( EventSim eventSim ) {
        // For the purposes of the simulation I'm showing 3 assets per event and 3 rules per asset. So each event generates 9 tasks.
        // The flatMap does the multiplication by 3.
        // The mapToObj does the conversion to EventTaskDPContext.
        return eventSim.assetIds()
                       .stream()
                       .flatMap( assetId -> IntStream.range( 0, 3 ).mapToObj( i -> new EventTaskDPContext( eventSim, assetId, "rule" + i ) ) );
    }
}
