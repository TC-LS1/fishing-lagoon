package com.drpicox.fishingLagoon.business.bots;

public class Bot {
    private final BotId id;
    private String name;

    public Bot(BotId id) {
        this(id, "NoName");
    }

    public Bot(BotId id, String name) {
        this.id = id;
        this.name = name;
    }

    public BotId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void update(String name) {
        this.name = name;
    }
}
