package com.drpicox.fishingLagoon.parser;

import com.drpicox.fishingLagoon.rounds.RoundId;
import com.google.gson.*;

import java.lang.reflect.Type;

public class RoundIdGsonAdapter implements JsonSerializer<RoundId>, JsonDeserializer<RoundId> {
    @Override
    public JsonElement serialize(RoundId roundId, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(roundId.getValue());
    }

    @Override
    public RoundId deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new RoundId(jsonElement.getAsJsonPrimitive().getAsString());
    }
}
