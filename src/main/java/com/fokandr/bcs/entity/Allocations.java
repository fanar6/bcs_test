package com.fokandr.bcs.entity;

import java.util.List;

public class Allocations {
    private double value;
    private List<Allocation> allocations;

    public Allocations() {
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<Allocation> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<Allocation> allocations) {
        this.allocations = allocations;
    }
}
