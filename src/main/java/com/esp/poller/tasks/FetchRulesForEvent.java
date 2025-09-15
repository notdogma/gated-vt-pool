package com.esp.poller.tasks;

import com.esp.poller.model.EventSim;
import com.esp.poller.ruleCache.RuleCacheSim;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This is a simulation of fetching rules from the rule cache.
 * For the purposes of the simulation, I'm just generating rules.
 * Put real code here to fetch rules from the rule cache.
 */
public class FetchRulesForEvent implements Function<EventSim, Stream<ClientTask>> {
    @Override
    public Stream<ClientTask> apply( EventSim eventSim ) {
        return new RuleCacheSim().getRules( eventSim ).map( DPTaskSim::new );
    }
}
