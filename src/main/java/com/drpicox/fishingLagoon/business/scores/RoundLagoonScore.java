package com.drpicox.fishingLagoon.business.scores;

public class RoundLagoonScore {
    private long fishPopulation;

    public RoundLagoonScore() {
    }

    public RoundLagoonScore(RoundLagoonScore sample) {
        this.fishPopulation = sample.fishPopulation;
    }

    public void setFishPopulation(long fishPopulation) {
        this.fishPopulation = fishPopulation;
    }

    public long getFishPopulation() {
        return fishPopulation;
    }
}
