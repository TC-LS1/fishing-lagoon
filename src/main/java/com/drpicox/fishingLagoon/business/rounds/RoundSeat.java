package com.drpicox.fishingLagoon.business.rounds;

public class RoundSeat {
    private int lagoonIndex;

    public RoundSeat() {
    }

    public RoundSeat(int lagoonIndex) {
        this.lagoonIndex = lagoonIndex;
    }

    public void setLagoonIndex(int lagoonIndex, int lagoonCount) {
        if (lagoonIndex < 0) throw new IllegalArgumentException("Seat lagoonIndex cannot be negative, it was: " + lagoonIndex);
        if (lagoonIndex >= lagoonCount) throw new IllegalArgumentException("Seat lagoonIndex cannot be greater than available lagoons, it was " + lagoonIndex + " and lagoon count was " + lagoonIndex);

        this.lagoonIndex = lagoonIndex;
    }

    public int getLagoonIndex() {
        return lagoonIndex;
    }
}