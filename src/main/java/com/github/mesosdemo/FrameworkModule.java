package com.github.mesosdemo;

import com.github.mesosdemo.framework.ExecutorInfoProvider;
import com.github.mesosdemo.metrics.StatsdClientProvider;
import com.github.mesosdemo.task.DDnsRunnable;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.timgroup.statsd.StatsDClient;
import org.apache.mesos.Protos;

import javax.inject.Singleton;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FrameworkModule extends AbstractModule {

    private static final String PROPERTIES_FILE = "application.properties";

    @Override
    protected void configure() {
        bind(StatsDClient.class).toProvider(StatsdClientProvider.class).in(Singleton.class);
        bind(Protos.ExecutorInfo.class).toProvider(ExecutorInfoProvider.class).in(Singleton.class);
        bind(ScheduledExecutorService.class).toInstance(Executors.newSingleThreadScheduledExecutor());
        bind(Runnable.class).to(DDnsRunnable.class).asEagerSingleton();

        Names.bindProperties(binder(), readProperties());
    }

    private Properties readProperties() {
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(getClass().getClassLoader()
                                                                      .getResourceAsStream(PROPERTIES_FILE))) {
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
