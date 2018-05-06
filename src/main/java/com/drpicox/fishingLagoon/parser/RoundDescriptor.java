package com.drpicox.fishingLagoon.parser;

import java.util.*;

public class RoundDescriptor {

    private long seatMilliseconds = 20000L;
    private long commandMilliseconds = 20000L;
    private long scoreMilliseconds = 20000L;
    private double maxDensity;
    private List<LagoonDescriptor> lagoonDescriptors;
    private int weekCount;

    public RoundDescriptor(long seatMilliseconds, long commandMilliseconds, long scoreMilliseconds, double maxDensity, List<LagoonDescriptor> lagoonDescriptors, int weekCount) {
        this.scoreMilliseconds = scoreMilliseconds;
        this.commandMilliseconds = commandMilliseconds;
        this.seatMilliseconds = seatMilliseconds;
        this.maxDensity = maxDensity;
        this.lagoonDescriptors = new ArrayList<>(lagoonDescriptors);
        this.weekCount = weekCount;
    }

    public double getMaxDensity() {
        return maxDensity;
    }

    public LagoonDescriptor getLagoonDescriptor(int lagoonIndex) {
        return lagoonDescriptors.get(lagoonIndex % lagoonDescriptors.size());
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

    public Map<String,Object> toMap() {
        var lagoons = new LinkedList<Object>();
        for (var lagoon: lagoonDescriptors) {
            lagoons.add(lagoon.toMap());
        }

        var result = new LinkedHashMap<String,Object>();
        result.put("weekCount", weekCount);
        result.put("maxDensity", maxDensity);
        result.put("scoreMilliseconds", scoreMilliseconds);
        result.put("commandMilliseconds", commandMilliseconds);
        result.put("seatMilliseconds", seatMilliseconds);
        result.put("lagoons", lagoons);
        return result;
    }

    public int getLagoonCount() {
        return lagoonDescriptors.size();
    }
}
