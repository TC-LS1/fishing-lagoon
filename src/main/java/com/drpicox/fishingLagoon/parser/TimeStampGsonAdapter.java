package com.drpicox.fishingLagoon.parser;

import com.drpicox.fishingLagoon.common.TimeStamp;
import com.google.gson.*;

import java.lang.reflect.Type;

public class TimeStampGsonAdapter implements JsonSerializer<TimeStamp>, JsonDeserializer<TimeStamp> {
    @Override
    public JsonElement serialize(TimeStamp timeStamp, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(timeStamp.getMilliseconds());
    }

    @Override
    public TimeStamp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new TimeStamp(jsonElement.getAsJsonPrimitive().getAsLong());
    }
}
