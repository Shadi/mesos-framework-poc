package com.github.mesosdemo.task;

import com.github.mesosdemo.metrics.MetricsService;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class DDnsRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DDnsRunnable.class);
    private static final String REQUEST_METRIC = "request_sent";
    private static final String REQUEST_ERROR = "request_error";

    private final String url;
    private final String domain;
    private final String token;

    private final MetricsService metricsService;

    @Inject
    public DDnsRunnable(@Named("app.ddns.url") String url,
                        @Named("app.ddns.domain") String domain,
                        @Named("app.ddns.token") String token,
                        MetricsService metricsService) {
        this.url = url;
        this.domain = domain;
        this.token = token;
        this.metricsService = metricsService;
    }

    @Override
    public void run() {
        try {
            Unirest.get(url)
                    .queryString("domains", domain)
                    .queryString("token", token)
                    .asString();
            metricsService.increment(REQUEST_METRIC);
        } catch (UnirestException e) {
            logger.error("error sending request", e);
            metricsService.increment(REQUEST_ERROR);
        }
    }
}
