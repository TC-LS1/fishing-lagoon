package com.drpicox.fishingLagoon.parser;

import com.drpicox.fishingLagoon.engine.RoundCommands;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class RoundCommandsGsonAdapter implements JsonSerializer<RoundCommands> {

    @Override
    public JsonElement serialize(RoundCommands roundCommands, Type type, JsonSerializationContext context) {
        return context.serialize(roundCommands.getCommands());
    }
}
