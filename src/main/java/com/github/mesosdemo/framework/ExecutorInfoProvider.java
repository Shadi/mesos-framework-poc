package com.github.mesosdemo.framework;

import org.apache.mesos.Protos;

import javax.inject.Provider;

public class ExecutorInfoProvider implements Provider<Protos.ExecutorInfo> {

    private final String command = "java -cp mesos-demo-0.0.1-SNAPSHOT-jar-with-dependencies.jar " +
            "com.github.mesosdemo.framework.SchedulingMesoExecutor";

    @Override
    public Protos.ExecutorInfo get() {

        String path = System.getProperty("user.dir") + "/mesos-demo-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
        Protos.CommandInfo.URI uri = Protos.CommandInfo.URI.newBuilder().setValue(path).setExtract(false).build();

        Protos.CommandInfo commandInfo = Protos.CommandInfo.newBuilder()
                .setValue(command)
                .addUris(uri)
                .build();

        return Protos.ExecutorInfo.newBuilder()
                .setExecutorId(Protos.ExecutorID.newBuilder().setValue("SchedulingMesosExecutor"))
                .setCommand(commandInfo)
                .setName("SchedulingMesosExecutor Java")
                .setSource("java")
                .build();

    }
}
