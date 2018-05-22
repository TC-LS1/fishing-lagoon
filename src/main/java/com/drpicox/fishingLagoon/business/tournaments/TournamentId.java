package com.drpicox.fishingLagoon.business.tournaments;

import java.util.Objects;

public class TournamentId {

    public static final TournamentId SPARRING = new TournamentId("SPARRING");
    private String value;

    public TournamentId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentId roundId = (TournamentId) o;
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
