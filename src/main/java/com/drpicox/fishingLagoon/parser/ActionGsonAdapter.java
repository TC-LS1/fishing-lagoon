package com.drpicox.fishingLagoon.parser;

import com.drpicox.fishingLagoon.actions.Action;
import com.drpicox.fishingLagoon.actions.ActionParser;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ActionGsonAdapter implements JsonSerializer<Action>, JsonDeserializer<Action> {

    private ActionParser actionParser;

    public ActionGsonAdapter(ActionParser actionParser) {
        this.actionParser = actionParser;
    }

    @Override
    public JsonElement serialize(Action action, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(action.toString());
    }

    @Override
    public Action deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return actionParser.parseAction(jsonElement.getAsJsonPrimitive().getAsString());
    }
}
