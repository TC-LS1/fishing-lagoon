package com.drpicox.fishingLagoon.business.rounds;

import com.drpicox.fishingLagoon.common.TimeOffset;

import java.util.*;

public class RoundDescriptor {

    private long seatMilliseconds;
    private long commandMilliseconds;
    private long scoreMilliseconds;
    private double maxDensity;
    private List<LagoonDescriptor> lagoons;
    private int weekCount;

    public RoundDescriptor(long seatMilliseconds, long commandMilliseconds, long scoreMilliseconds, double maxDensity, List<LagoonDescriptor> lagoonDescriptors, int weekCount) {
        this.scoreMilliseconds = scoreMilliseconds;
        this.commandMilliseconds = commandMilliseconds;
        this.seatMilliseconds = seatMilliseconds;
        this.maxDensity = maxDensity;
        this.lagoons = new ArrayList<>(lagoonDescriptors);
        this.weekCount = weekCount;
    }

    public double getMaxDensity() {
        return maxDensity;
    }

    public LagoonDescriptor getLagoonDescriptor(int lagoonIndex) {
        return lagoons.get(lagoonIndex % lagoons.size());
    }

    public long getSeatMilliseconds() {
        return seatMilliseconds;
    }

    public long getCommandMilliseconds() {
        return commandMilliseconds;
    }

    public long getScoreMilliseconds() {
        return scoreMilliseconds;
    }

    public long getTotalMilliseconds() {
        return seatMilliseconds + commandMilliseconds + scoreMilliseconds;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public int getLagoonCount() {
        return lagoons.size();
    }

    public TimeOffset getFinishOffset() {
        return new TimeOffset(getTotalMilliseconds());
    }

    public TimeOffset getSeatOffset() {
        return new TimeOffset(0);
    }

    public TimeOffset getCommandOffset() {
        return new TimeOffset(0 + seatMilliseconds);
    }

    public TimeOffset getScoreOffset() {
        return new TimeOffset(0 + seatMilliseconds + commandMilliseconds);
    }

    @Override
    public String toString() {
        return "RoundDescriptor{" +
                "\n    seatMilliseconds=" + seatMilliseconds +
                "\n  , commandMilliseconds=" + commandMilliseconds +
                "\n  , scoreMilliseconds=" + scoreMilliseconds +
                "\n  , maxDensity=" + maxDensity +
                "\n  , lagoons=" + lagoons +
                "\n  , weekCount=" + weekCount +
                '}';
    }
}
