package com.drpicox.fishingLagoon.common.parser;

import java.util.Map;

public class Props {

    private Map<String,String> props;

    public Props(Map<String, String> props) {
        this.props = props;
    }

    public String[] getCsv(String key, String... absent) {
        String value = props.get(key);
        if (value == null) return absent;

        return value.split(",");
    }

    public Integer getInteger(String key, Integer absent) {
        String value = props.get(key);
        if (value == null) return absent;

        return Integer.parseInt(value);
    }

    public Long getLong(String key, Long absent) {
        String value = props.get(key);
        if (value == null) return absent;

        return Long.parseLong(value);
    }

    public Double getDouble(String key, Double absent) {
        String value = props.get(key);
        if (value == null) return absent;

        return Double.parseDouble(value);
    }
}
