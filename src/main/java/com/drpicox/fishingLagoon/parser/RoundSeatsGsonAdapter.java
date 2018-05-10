package com.drpicox.fishingLagoon.parser;

import com.drpicox.fishingLagoon.engine.RoundSeats;
import com.google.gson.*;

import java.lang.reflect.Type;

public class RoundSeatsGsonAdapter implements JsonSerializer<RoundSeats> {

    @Override
    public JsonElement serialize(RoundSeats roundSeats, Type type, JsonSerializationContext context) {
        return context.serialize(roundSeats.getSeats());
    }
}
