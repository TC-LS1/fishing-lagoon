package com.drpicox.fishingLagoon.parser;

import com.drpicox.fishingLagoon.actions.Action;
import com.drpicox.fishingLagoon.actions.ActionParser;
import com.drpicox.fishingLagoon.bots.BotId;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.engine.RoundCommands;
import com.drpicox.fishingLagoon.engine.RoundSeats;
import com.drpicox.fishingLagoon.rounds.RoundId;
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
                    .registerTypeAdapter(TimeStamp.class, new TimeStampGsonAdapter())
                    .registerTypeAdapter(BotId.class, new BotIdGsonAdapter())
                    .registerTypeAdapter(RoundId.class, new RoundIdGsonAdapter())
                    .registerTypeAdapter(Action.class, new ActionGsonAdapter(actionParser))
                    .registerTypeAdapter(RoundSeats.class, new RoundSeatsGsonAdapter())
                    .registerTypeAdapter(RoundCommands.class, new RoundCommandsGsonAdapter())
                    .create();
        }
        return gson;
    }

}
