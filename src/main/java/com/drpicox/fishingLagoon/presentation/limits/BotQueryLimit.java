package com.drpicox.fishingLagoon.presentation.limits;

import com.drpicox.fishingLagoon.common.TimeStamp;

public class BotQueryLimit {

    private static final long TIME_LIMIT = 1000L;
    private static final long TIME_PENALISATION = 60000L;

    private long windowMilliseconds0 = Long.MIN_VALUE;
    private long windowMilliseconds1 = Long.MIN_VALUE;
    private long windowMilliseconds2 = Long.MIN_VALUE;
    private long windowMilliseconds3 = Long.MIN_VALUE;
    private long windowMilliseconds4 = Long.MIN_VALUE;
    private long penalisedUntil = Long.MIN_VALUE;


    public void verifyAccess(TimeStamp ts) {
        var milliseconds = ts.getMilliseconds();

        if (penalisedUntil > milliseconds) {
            throw new IllegalStateException("Bot query limit was exceeded and now it has to wait an extra penalisation");
        }

        if (windowMilliseconds0 > milliseconds - TIME_LIMIT) {
            penalisedUntil = windowMilliseconds0 + TIME_PENALISATION;
            throw new IllegalStateException("Bot query limit exceeded");
        }
    }

    public void trackAccess(TimeStamp ts) {
        verifyAccess(ts);

        var milliseconds = ts.getMilliseconds();
        shiftWindow(milliseconds);
    }

    private void shiftWindow(long nextMilliseconds) {
        windowMilliseconds0 = windowMilliseconds1;
        windowMilliseconds1 = windowMilliseconds2;
        windowMilliseconds2 = windowMilliseconds3;
        windowMilliseconds3 = windowMilliseconds4;
        windowMilliseconds4 = nextMilliseconds;
    }
}
