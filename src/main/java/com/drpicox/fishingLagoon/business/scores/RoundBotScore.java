package com.drpicox.fishingLagoon.business.scores;

public class RoundBotScore {
    private long score;

    public void setScore(long score) {
        this.score = score;
    }

    public long getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "RoundBotScore{" +
                "score=" + score +
                '}';
    }
}
