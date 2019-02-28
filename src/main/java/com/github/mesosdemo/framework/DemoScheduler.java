package com.github.mesosdemo.framework;

import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class DemoScheduler implements Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(DemoScheduler.class);
    private static final AtomicBoolean taskScheduled = new AtomicBoolean(false);

    private final TaskProvider taskProvider;

    @Inject
    public DemoScheduler(TaskProvider taskProvider) {
        this.taskProvider = taskProvider;
    }

    @Override
    public void registered(SchedulerDriver driver, Protos.FrameworkID frameworkId, Protos.MasterInfo masterInfo) {
        logger.info("scheduler registered with {}:{}", masterInfo.getHostname(), masterInfo.getPort());
    }

    @Override
    public void reregistered(SchedulerDriver driver, Protos.MasterInfo masterInfo) {
        logger.info("scheduler reregistered with {}:{}", masterInfo.getHostname(), masterInfo.getPort());
    }

    @Override
    public void resourceOffers(SchedulerDriver driver, List<Protos.Offer> offers) {
        for (Protos.Offer offer : offers) {
            Optional<Protos.TaskInfo> optionalTask = taskProvider.getTask(offer);
            if (!taskScheduled.get() && optionalTask.isPresent()) {
                taskScheduled.set(true);
                Protos.TaskInfo taskInfo = optionalTask.get();
                driver.launchTasks(Collections.singleton(offer.getId()),
                                   Collections.singleton(taskInfo));
            }
        }
    }

    @Override
    public void offerRescinded(SchedulerDriver driver, Protos.OfferID offerId) {

    }

    @Override
    public void statusUpdate(SchedulerDriver driver, Protos.TaskStatus status) {
        logger.info("Status updated: {}", status.getState().getDescriptorForType());
    }

    @Override
    public void frameworkMessage(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId,
                                 byte[] data) {
        logger.info("Received message {} on slave {}", new String(data), slaveId.getValue());
    }

    @Override
    public void disconnected(SchedulerDriver driver) {

    }

    @Override
    public void slaveLost(SchedulerDriver driver, Protos.SlaveID slaveId) {

    }

    @Override
    public void executorLost(SchedulerDriver driver, Protos.ExecutorID executorId, Protos.SlaveID slaveId, int status) {

    }

    @Override
    public void error(SchedulerDriver driver, String message) {
        logger.error(message);
    }
}
