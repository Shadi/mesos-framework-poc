package com.github.mesosdemo.service;

import org.apache.mesos.Protos;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ResourceService {

    @Inject
    public ResourceService() {
    }

    public double findOfferedCpu(Protos.Offer offer) {
        return findOfferedResourceCount(offer, "cpus");
    }

    public double findOfferedMemory(Protos.Offer offer) {
        return findOfferedResourceCount(offer, "mem");
    }

    private double findOfferedResourceCount(Protos.Offer offer, String resourceName) {
        double offered = 0;
        List<Protos.Resource> resourcesList = offer.getResourcesList();
        for (Protos.Resource resource : resourcesList) {
            if (resource.getName().equals(resourceName)){
                offered += resource.getScalar().getValue();
            }
        }
        return offered;
    }
}
