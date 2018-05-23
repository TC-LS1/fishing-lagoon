package com.drpicox.fishingLagoon.business.rounds;

import com.drpicox.fishingLagoon.business.bots.BotId;

import java.util.*;

public class RoundSeats {

    private Map<BotId, RoundSeat> seats;

    public RoundSeats() {
        seats = new HashMap<>();
    }

    public RoundSeats(Map<BotId, RoundSeat> seats) {
        this.seats = seats;
    }

    public int count() {
        return seats.size();
    }

    public Set<BotId> getBots() {
        return seats.keySet();
    }


    public Set<BotId> getLagoonBots(int lagoonIndex) {
        Set<BotId> result = new HashSet<>(getBots());
        result.removeIf(b -> get(b).getLagoonIndex() != lagoonIndex);
        return result;
    }

    public RoundSeat get(BotId bot) {
        return seats.get(bot);
    }

    public boolean isSeated(BotId bot) {
        return seats.containsKey(bot);
    }


    public void seatBot(BotId bot, int lagoonIndex, int lagoonCount) {
        var seat = seats.get(bot);
        if (seat == null) {
            seat = new RoundSeat();
            seats.put(bot, seat);
        }

        seat.setLagoonIndex(lagoonIndex, lagoonCount);
    }

    @Override
    public String toString() {
        return "RoundSeats{" +
                "seats=" + seats +
                '}';
    }
}
