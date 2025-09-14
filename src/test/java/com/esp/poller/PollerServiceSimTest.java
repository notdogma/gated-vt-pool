package com.esp.poller;

import org.junit.jupiter.api.Test;

class PollerServiceSimTest {
    @Test
    void testPollerServiceSim() throws InterruptedException {
        PollerServiceSim pollerServiceSim = new PollerServiceSim( 10000 );
        pollerServiceSim.start();

        Thread.sleep( 12000 );

        pollerServiceSim.stop();
    }
}
