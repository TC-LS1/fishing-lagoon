package com.drpicox.fishingLagoon.business.rounds;

import java.util.Objects;

public class RoundId {

    private String value;

    public RoundId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoundId roundId = (RoundId) o;
        return Objects.equals(value, roundId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
