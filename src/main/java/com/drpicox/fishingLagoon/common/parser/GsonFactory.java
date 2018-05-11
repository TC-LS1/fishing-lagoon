package com.drpicox.fishingLagoon.common.parser;

import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.common.actions.ActionParser;
import com.google.gson.*;

public class GsonFactory {

    private ActionParser actionParser;

    public GsonFactory(ActionParser actionParser) {
        this.actionParser = actionParser;
    }

    private Gson gson;
    public Gson get() {
        if (gson == null) {
            gson = new GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(Action.class, new ActionGsonAdapter(actionParser))
                    .create();
        }
        return gson;
    }

}
