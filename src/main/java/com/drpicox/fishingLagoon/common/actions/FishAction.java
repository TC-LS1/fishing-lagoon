package com.drpicox.fishingLagoon.common.actions;

import java.util.Objects;

public class FishAction extends Action {

    private long fishingCount;

    public FishAction(long fishingCount) {
        super();

        this.fishingCount = fishingCount;
    }

    public long getFishingCount() {
        return fishingCount;
    }

    @Override
    public long getFishValue() {
        return getFishingCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FishAction that = (FishAction) o;
        return fishingCount == that.fishingCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fishingCount);
    }

    @Override
    public String toString() {
        return "fish " + fishingCount;
    }
}
