package com.drpicox.fishingLagoon.presentation;

import com.drpicox.fishingLagoon.business.bots.Bot;
import com.drpicox.fishingLagoon.business.bots.BotId;

public class BotPresentation {

    private String id;
    private String name;

    public BotPresentation(Bot bot) {
        this.id = bot.getId().getValue();
        this.name = bot.getName();
    }

    public BotId getId() {
        return new BotId(id);
    }

    public String getName() {
        return name;
    }
}
