package com.drpicox.fishingLagoon.business.bots;

import java.util.Objects;

public class BotToken {

    private String value;

    public BotToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotToken botToken = (BotToken) o;
        return Objects.equals(value, botToken.value);
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
