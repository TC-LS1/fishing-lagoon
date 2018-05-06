package com.drpicox.fishingLagoon.parser;

import java.util.LinkedHashMap;
import java.util.Map;

public class LagoonDescriptor {

    private long fishPopulation;

    public LagoonDescriptor(long fishPopulation) {
        this.fishPopulation = fishPopulation;
    }

    public long getFishPopulation() {
        return fishPopulation;
    }

    public Map<String,Object> toMap() {
        var result = new LinkedHashMap<String,Object>();
        result.put("fishPopulation", fishPopulation);
        return result;
    }
}
