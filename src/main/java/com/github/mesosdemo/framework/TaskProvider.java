package com.github.mesosdemo.framework;

import com.github.mesosdemo.service.ResourceService;
import org.apache.mesos.Protos;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class TaskProvider {

    private static final double TASK_CPU = 0.1;
    private static final double TASK_MEM = 16;

    private final ResourceService resourceService;
    private final Protos.ExecutorInfo executorInfo;

    @Inject
    public TaskProvider(ResourceService resourceService, Protos.ExecutorInfo executorInfo) {
        this.resourceService = resourceService;
        this.executorInfo = executorInfo;
    }

    Optional<Protos.TaskInfo> getTask(Protos.Offer offer) {
        if (!offerHasEnoughResources(offer)) {
            return Optional.empty();
        }

        Protos.TaskID taskID = Protos.TaskID.newBuilder().setValue(UUID.randomUUID().toString()).build();


        Protos.TaskInfo task = Protos.TaskInfo.newBuilder()
                .setName("task " + taskID)
                .setTaskId(taskID)
                .setSlaveId(offer.getSlaveId())
                .addResources(Protos.Resource.newBuilder()
                                      .setName("cpus")
                                      .setType(Protos.Value.Type.SCALAR)
                                      .setScalar(Protos.Value.Scalar.newBuilder().setValue(TASK_CPU)))
                .addResources(Protos.Resource.newBuilder()
                                      .setName("mem")
                                      .setType(Protos.Value.Type.SCALAR)
                                      .setScalar(Protos.Value.Scalar.newBuilder().setValue(TASK_MEM)))
                .setExecutor(executorInfo)
                .build();
        return Optional.of(task);
    }

    private boolean offerHasEnoughResources(Protos.Offer offer) {
        return resourceService.findOfferedCpu(offer) >= TASK_CPU &&
                resourceService.findOfferedMemory(offer) > TASK_MEM;
    }
}
