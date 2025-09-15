package com.esp.poller.tasks;

import com.esp.poller.model.EventSim;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This is a simulation of fetching events from the logger.
 * For the purposes of the simulation, I'm just generating events.
 * Put real code here to fetch events from the logger.
 */
public class FetchEventsFromLogger implements Function<Integer, Stream<EventSim>> {
    @Override
    public Stream<EventSim> apply( Integer allowedTasks ) {
        // Put real code here to fetch events from the logger.
        // For the purposes of the simulation, I'm just generating events.
        return IntStream.range( 0, allowedTasks / 10 )
                        // this is the event
                        .mapToObj( i -> new EventSim( "event" + i, new ArrayList<>( List.of( "asset1", "asset2", "asset3" ) ) ) );
    }
}
