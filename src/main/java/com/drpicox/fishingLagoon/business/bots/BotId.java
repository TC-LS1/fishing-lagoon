package com.drpicox.fishingLagoon.business.bots;

import java.util.Objects;

public class BotId implements Comparable<BotId> {

    private final String value;

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotId botId = (BotId) o;
        return Objects.equals(value, botId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(BotId o) {
        return value.compareTo(o.value);
    }

    public BotId(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
