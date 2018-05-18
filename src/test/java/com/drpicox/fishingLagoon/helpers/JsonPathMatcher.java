package com.drpicox.fishingLagoon.helpers;

import com.nebhale.jsonpath.JsonPath;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class JsonPathMatcher extends BaseMatcher {

    public static JsonPathMatcher jsonPath(String path, Matcher matcher) {
        return new JsonPathMatcher(path, matcher);
    }
    public static JsonPathMatcher jsonPath(String path, Object value) {
        return new JsonPathMatcher(path, value);
    }


    private String jsonPath;
    private Object value;
    private Matcher matcher;
    private Object lastMatch;

    public JsonPathMatcher(String jsonPath, Object value) {
        this.jsonPath = jsonPath;
        this.value = value;
    }

    public JsonPathMatcher(String jsonPath, Matcher matcher) {
        this.jsonPath = jsonPath;
        this.matcher = matcher;
    }

    @Override
    public boolean matches(Object o) {
        Object ob = JsonPath.read(jsonPath, o.toString(), Object.class);
        lastMatch = ob;
        if (matcher != null) {
            return matcher.matches(ob);
        }
        if (ob == null || value == null) {
            return ob == value;
        }
        return ob.equals(value);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(jsonPath);
        description.appendText(" = ");
        if (matcher != null) {
            matcher.describeTo(description);
        } else {
            description.appendValue(value);
        }
        description.appendText(" was: ");
        description.appendValue(lastMatch);
    }
}
