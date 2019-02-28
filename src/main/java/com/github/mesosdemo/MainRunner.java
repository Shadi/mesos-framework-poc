package com.github.mesosdemo;

import com.github.mesosdemo.framework.DemoScheduler;
import org.apache.mesos.Executor;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MainRunner {

    private final DemoScheduler scheduler;

    @Inject
    public MainRunner(DemoScheduler scheduler) {
        this.scheduler = scheduler;
    }

    void run(String master){
        Protos.FrameworkInfo.Builder frameworkBuilder = Protos.FrameworkInfo.newBuilder()
                .setFailoverTimeout(60000)
                .setUser("")
                .setName("Scheduling Framework");

        frameworkBuilder.setCheckpoint(true);

        frameworkBuilder.setPrincipal("scheduling-framework-java");
        MesosSchedulerDriver schedulerDriver = new MesosSchedulerDriver(scheduler, frameworkBuilder.build(), master);
        int status = schedulerDriver.run() == Protos.Status.DRIVER_STOPPED ? 0 : 1;
        schedulerDriver.stop();
        System.exit(status);
    }
}
