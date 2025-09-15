package com.esp.poller.tasks;

import com.esp.poller.model.EventSim;
import com.esp.poller.model.EventTaskEPContext;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Put real code here to create tasks for an event.
 */
public class CreateAllTasksForEvent implements Function<EventSim, Stream<ClientTask>> {
    @Override
    public Stream<ClientTask> apply( EventSim eventSim ) {
        Stream<ClientTask> dpStream = new FetchRulesForEvent().apply( eventSim );

        // this simulates that the event is a process complete event
        if( Math.random() > .5 ) {
            Stream<ClientTask> epStream = Stream.of( new EPTaskSim( new EventTaskEPContext(
                    eventSim ) ) );

            return Stream.concat( dpStream, epStream );
        } else
            return dpStream;
    }
}
