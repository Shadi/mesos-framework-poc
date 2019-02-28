package com.github.mesosdemo.metrics;

import com.timgroup.statsd.StatsDClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MetricsService {

    private final StatsDClient statsDClient;

    @Inject
    MetricsService(StatsDClient statsDClient) {
        this.statsDClient = statsDClient;
    }

    public void increment(String counterName){
        statsDClient.increment(counterName);
    }
}
