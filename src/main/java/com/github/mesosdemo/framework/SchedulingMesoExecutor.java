package com.github.mesosdemo.framework;

import com.github.mesosdemo.FrameworkModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.mesos.Executor;
import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.MesosExecutorDriver;
import org.apache.mesos.Protos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

@Singleton
public class SchedulingMesoExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingMesoExecutor.class);

    private final ScheduledExecutorService scheduledExecutorService;
    private final Runnable runnable;
    private final int scheduleFreq;

    @Inject
    public SchedulingMesoExecutor(ScheduledExecutorService scheduledExecutorService,
                                  Runnable runnable,
                                  @Named("app.ddns.request.freq") int scheduleFreq) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.runnable = runnable;
        this.scheduleFreq = scheduleFreq;
    }

    @Override
    public void registered(ExecutorDriver driver, Protos.ExecutorInfo executorInfo, Protos.FrameworkInfo frameworkInfo,
                           Protos.SlaveInfo slaveInfo) {
        logger.info("scheduler registered with {}:{}", slaveInfo.getHostname(), slaveInfo.getPort());
    }

    @Override
    public void reregistered(ExecutorDriver driver, Protos.SlaveInfo slaveInfo) {
        logger.info("scheduler reregistered with {}:{}", slaveInfo.getHostname(), slaveInfo.getPort());
    }

    @Override
    public void disconnected(ExecutorDriver driver) {

    }

    @Override
    public void launchTask(ExecutorDriver driver, Protos.TaskInfo task) {
        Protos.TaskStatus runningStatus = Protos.TaskStatus.newBuilder()
                .setTaskId(task.getTaskId())
                .setState(Protos.TaskState.TASK_RUNNING).build();
        driver.sendStatusUpdate(runningStatus);

        logger.info("Submitting task to scheduling executor");

        scheduledExecutorService.scheduleAtFixedRate(runnable, 0, scheduleFreq, TimeUnit.MINUTES);

        driver.sendFrameworkMessage("Task submitted".getBytes());

        Protos.TaskStatus finishedStatus = Protos.TaskStatus.newBuilder()
                .setTaskId(task.getTaskId())
                .setState(Protos.TaskState.TASK_FINISHED).build();
        driver.sendStatusUpdate(finishedStatus);
    }

    @Override
    public void killTask(ExecutorDriver driver, Protos.TaskID taskId) {
        logger.info("killing task: {}", taskId.getValue());
    }

    @Override
    public void frameworkMessage(ExecutorDriver driver, byte[] data) {
        logger.info("received message: {}", new String(data, UTF_8));
    }

    @Override
    public void shutdown(ExecutorDriver driver) {
        scheduledExecutorService.shutdown();
        logger.info("SchedulingMesoExecutor is shutting down");
    }

    @Override
    public void error(ExecutorDriver driver, String message) {
        logger.error(message);
    }

    public static void main(String[] args){
        Injector injector = Guice.createInjector(new FrameworkModule());
        Executor executor = injector.getInstance(SchedulingMesoExecutor.class);
        MesosExecutorDriver driver = new MesosExecutorDriver(executor);
        System.exit(driver.run() == Protos.Status.DRIVER_STOPPED ? 0 : 1);
    }
}
