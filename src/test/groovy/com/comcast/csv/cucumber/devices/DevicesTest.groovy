package com.comcast.csv.cucumber.devices

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse

import org.testng.annotations.Test

class DevicesTest {

    @Test
    void 'Verify Thread Unique Values'() { 
        List threads = new ArrayList()

        threads.push(new TestThread(expected: "apples"));
        threads.push(new TestThread(expected: "bananas"));
        threads.push(new TestThread(expected: "carrots"));
        threads.push(new TestThread(expected: "diapers"));
        threads.push(new TestThread(expected: "elephants"));

        threads.each { it.start() }
        threads.each { it.join() }

        threads.each { assertFalse(it.fail) }
    }

    class TestThread extends Thread {
        String expected;
        boolean fail = true

        @Override
        public void run() {
            Devices.setLocal(expected)
            assertEquals(Devices.getLocal(), expected)

            for (int i = 0; i < 10; i++) {
                Thread.sleep((int) Math.random() * 50)
                assertEquals(Devices.getLocal(), expected)
            }

            Devices.releaseLocal()
            Thread.sleep((int) Math.random() * 50)
            assertEquals(Devices.getLocal(), null)

            fail = false
        }
    }
}
