package com.drpicox.fishingLagoon.engine;

import com.drpicox.fishingLagoon.actions.Action;
import com.drpicox.fishingLagoon.bots.BotId;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.parser.RoundDescriptor;
import com.drpicox.fishingLagoon.rounds.RoundId;
import com.drpicox.fishingLagoon.rules.FishingLagoonRules;

import java.util.List;

public class RoundEngine {
    private RoundId id;
    private TimeStamp startTs;
    private TimeStamp endTs;

    private RoundDescriptor descriptor;
    private RoundSeats seats;
    private RoundCommands commands;
    private RoundScores scores;

    private TimeStamp nowTs;
    private RoundTimeState state;
    private BotId selfId;

    public RoundEngine(RoundId id, TimeStamp startTs, RoundDescriptor descriptor) {
        this.id = id;
        this.startTs = startTs;
        this.endTs = startTs.plus(descriptor.getFinishOffset());
        this.descriptor = descriptor;

        seats = new RoundSeats();
        commands = new RoundCommands();
        scores = null; // computed under demand
    }

    public TimeStamp getStartTs() {
        return startTs;
    }

    // descriptor

    public RoundDescriptor getDescriptor() {
        return descriptor;
    }

    // time and round state


    public TimeStamp getNowTs() {
        return nowTs;
    }

    public RoundTimeState getState() {
        return state;
    }

    public void updateNow(TimeStamp nowTs) {
        this.nowTs = nowTs;
        this.state = RoundTimeState.get(nowTs.getOffsetFrom(startTs), descriptor);
    }

    // round seats

    public boolean seatBot(BotId botId, int lagoonIndex) {
        if (!state.isAcceptingSeats()) throw new IllegalStateException("It is not time for seating");
        return seats.seatBot(botId, lagoonIndex, descriptor.getMaxDensity());
    }

    public void forceSeatBot(BotId botId, int lagoonIndex) {
        seats.forceSeatBot(botId, lagoonIndex);
    }

    public RoundSeats getSeats() {
        return new RoundSeats(seats);
    }


    // lagoon count

    public int getLagoonCount() {
        return getLagoonCount(selfId);
    }

    public int getLagoonCount(BotId botId) {
        var maxDensity = descriptor.getMaxDensity();
        var lagoonCount = seats.getLagoonCount(botId, maxDensity);
        return lagoonCount;
    }

    // round commands

    public boolean commandBot(BotId botId, List<Action> actions) {
        if (!state.isAcceptingCommands()) throw new IllegalStateException("It is not time for commanding");
        if (actions.size() != getWeekCount()) throw new IllegalArgumentException("Actions length must match weekCount");

        var lagoonIndex = seats.getBotSeat(botId);
        if (lagoonIndex == -1) return false;

        return commands.commandBot(botId, actions);
    }

    int getWeekCount() {
        return descriptor.getWeekCount();
    }

    public void forceCommandBot(BotId botId, List<Action> actions) {
        commands.forceCommandBot(botId, actions);
    }

    public RoundCommands getCommands() {
        return new RoundCommands(commands);
    }



    public Action getAction(BotId bot, int weekIndex) {
        return commands.getAction(bot, weekIndex);
    }

    // scores

    public RoundScores getScores(FishingLagoonRules rules) {
        // compute if not computed, and save
        if (scores == null) {
            scores = rules.score(this);
        }

        return new RoundScores(scores);
    }

    // weeks
}