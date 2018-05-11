package com.drpicox.fishingLagoon.business.rounds;

public class LagoonDescriptor {

    private long fishPopulation;

    public LagoonDescriptor(long fishPopulation) {
        this.fishPopulation = fishPopulation;
    }

    public long getFishPopulation() {
        return fishPopulation;
    }
}
