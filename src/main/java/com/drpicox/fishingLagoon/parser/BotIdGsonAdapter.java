package com.drpicox.fishingLagoon.parser;

import com.drpicox.fishingLagoon.bots.BotId;
import com.google.gson.*;

import java.lang.reflect.Type;

public class BotIdGsonAdapter implements JsonSerializer<BotId>, JsonDeserializer<BotId> {
    @Override
    public JsonElement serialize(BotId botId, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(botId.getValue());
    }

    @Override
    public BotId deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new BotId(jsonElement.getAsJsonPrimitive().getAsString());
    }
}
