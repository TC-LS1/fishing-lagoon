package com.drpicox.fishingLagoon.common.parser;

import java.util.HashMap;
import java.util.Map;

public class PropsParser {

    public Props parse(String tournamentText) {
        Map<String,String> props = new HashMap<>();
        String[] lines = tournamentText.split("\n");
        for (String line: lines) {
            String[] parts = line.split("=");
            if (parts.length == 2) props.put(parts[0], parts[1]);
        }
        return new Props(props);
    }

}
