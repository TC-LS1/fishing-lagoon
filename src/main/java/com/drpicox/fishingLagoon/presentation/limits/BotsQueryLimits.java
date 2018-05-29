package com.drpicox.fishingLagoon.presentation.limits;

import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.common.TimeStamp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BotsQueryLimits {

    private boolean skipLimits = false;
    private ConcurrentMap<BotToken, BotQueryLimit> limits = new ConcurrentHashMap<>();

    public void setSkipLimits(boolean mode) {
        this.skipLimits = mode;
    }

    public void trackAccess(BotToken token, TimeStamp ts) {
        if (skipLimits) return;
        var queryLimit = limits.get(token);
        if (queryLimit == null) {
            queryLimit = new BotQueryLimit();
            limits.putIfAbsent(token, queryLimit);

            queryLimit = limits.get(token);
        }

        queryLimit.trackAccess(ts);
    }

    public void verifyAccess(BotToken token, TimeStamp ts) {
        if (skipLimits) return;
        var queryLimit = limits.get(token);
        if (queryLimit == null) return;

        queryLimit.verifyAccess(ts);
    }
}
