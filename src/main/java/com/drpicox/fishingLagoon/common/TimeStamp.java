package com.drpicox.fishingLagoon.common;

import java.util.Objects;

public class TimeStamp implements Comparable<TimeStamp> {
    private final long milliseconds;

    public TimeStamp(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public TimeStamp minus(TimeOffset to) {
        return new TimeStamp(this.milliseconds - to.getMilliseconds());
    }

    public TimeStamp plus(TimeOffset to) {
        return new TimeStamp(this.milliseconds + to.getMilliseconds());
    }

    public TimeOffset getOffsetFrom(TimeStamp originTs) {
        return new TimeOffset(milliseconds - originTs.milliseconds);
    }

    @Override
    public int compareTo(TimeStamp o) {
        return Long.compare(milliseconds, o.milliseconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeStamp timeStamp = (TimeStamp) o;
        return milliseconds == timeStamp.milliseconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(milliseconds);
    }

    @Override
    public String toString() {
        return "ts{" +
                milliseconds +
                '}';
    }
}
