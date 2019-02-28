package com.github.mesosdemo.metrics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class StatsdClientProvider implements Provider<StatsDClient> {

    private final String host;
    private final int port;
    private final String prefix;

    @Inject
    public StatsdClientProvider(@Named("metrics.statsd.host") String host,
                                @Named("metrics.statsd.port") int port,
                                @Named("metrics.prefix") String prefix) {
        this.host = host;
        this.port = port;
        this.prefix = prefix;
    }

    @Override
    public StatsDClient get() {
        return new NonBlockingStatsDClient(prefix, host, port);
    }
}
