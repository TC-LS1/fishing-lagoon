package com.drpicox.fishingLagoon.common.actions;

public class RestAction extends Action {

    public static final Action NOOP = new RestAction();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RestAction;
    }

    @Override
    public int hashCode() {
        return 7473;
    }

    @Override
    public String toString() {
        return "rest";
    }
}
