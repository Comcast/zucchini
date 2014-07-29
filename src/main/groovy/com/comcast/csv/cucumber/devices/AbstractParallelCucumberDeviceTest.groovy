package com.comcast.csv.cucumber.devices;

import java.util.concurrent.atomic.AtomicInteger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import cucumber.api.testng.TestNGCucumberRunner


abstract class AbstractParallelCucumberDeviceTest<T> {

    private static Logger logger = LoggerFactory.getLogger(AbstractParallelCucumberDeviceTest.class)

    private AtomicInteger count = new AtomicInteger(0)

    private List<T> devices

    @BeforeClass
    public void processDeviceList() {
        this.devices = getDevices();
    }

    @Test(threadPoolSize = 20, invocationCount = 20)
    public void run_cukes() throws IOException {
        int index = count.getAndIncrement();
        if (index < devices.size()) {
            T device = devices.get(index);
            Devices.setLocal(device);

            logger.debug("ParallelCucumberTest[${index}] for Device[${device}] starting")
            new TestNGCucumberRunner(getClass()).runCukes();
            logger.debug("ParallelCucumberTest[${index}] for Device[${device}] finished")
            cleanup(device)
        }
    }

    public abstract List<T> getDevices();

    void cleanup(T device) {
        logger.debug("Cleanup method was not implemented for ${device}")
    }
}
