package com.drpicox.fishingLagoon.presentation.limits;

import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.common.TimeStamp;

import java.util.HashMap;
import java.util.Map;

public class BotsQueryLimits {

    private boolean skipLimits = false;
    private Map<BotToken, BotQueryLimit> limits = new HashMap<>();

    public void setSkipLimits(boolean mode) {
        this.skipLimits = mode;
    }

    public void trackAccess(BotToken token, TimeStamp ts) {
        if (skipLimits) return;
        var queryLimit = limits.get(token);
        if (queryLimit == null) {
            queryLimit = new BotQueryLimit();
            limits.put(token, queryLimit);
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
