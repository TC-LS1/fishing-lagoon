package com.drpicox.fishingLagoon.engine;

import com.drpicox.fishingLagoon.bots.BotId;

import java.util.*;

public class RoundSeats {

    private Map<BotId, RoundSeat> seats = new HashMap<>();
    private Map<BotId, Integer> botSeats = new HashMap<>();

    public RoundSeats() {
    }

    RoundSeats(RoundSeats sample) {
        seats.putAll(sample.seats);
        botSeats.putAll(sample.botSeats);
    }

    public Map<BotId, RoundSeat> getSeats() {
        return seats;
    }

    public int getBotCount() {
        return seats.size();
    }

    public Set<BotId> getBots() {
        return new HashSet<>(seats.keySet());
    }


    public Set<Integer> getLagoonIndices() {
        var result = new HashSet<Integer>();
        for (var seat: seats.values()) {
            result.add(seat.getLagoonIndex());
        }
        return result;
    }


    public Set<BotId> getLagoonBots(int lagoonIndex) {
        Set<BotId> result = new HashSet<>();
        for (var bot: seats.keySet()) {
            var botLagoonIndex = seats.get(bot).getLagoonIndex();
            if (botLagoonIndex == lagoonIndex) {
                result.add(bot);
            }
        }
        return result;
    }

    public int getBotSeat(BotId botId) {
        var botSeat = seats.get(botId);
        if (botSeat == null) return -1;

        return botSeat.getLagoonIndex();
    }

    public int getLagoonCount(BotId botId, double maxDensity) {
        var botCount = getBotCount();
        var plusOne = botId == null || seats.containsKey(botId) ? 0 : 1;
        var result = (int)Math.ceil((botCount + plusOne) / maxDensity);
        return result;
    }


    boolean seatBot(BotId botId, int lagoonIndex, int lagoonCount) {
        var prevBotSeat = getBotSeat(botId);
        if (prevBotSeat == lagoonIndex) return false;

        if (lagoonIndex >= lagoonCount) return false;

        ensureBot(botId);
        seats.get(botId).setLagoonIndex(lagoonIndex);
        botSeats.put(botId, lagoonIndex);
        return true;
    }

    public void forceSeatBot(BotId botId, int lagoonIndex) {
        ensureBot(botId);
        seats.get(botId).setLagoonIndex(lagoonIndex);
        botSeats.put(botId, lagoonIndex);
    }

    private void ensureBot(BotId botId) {
        if (!seats.containsKey(botId)) {
            seats.put(botId, new RoundSeat());
        }
    }
}
