package com.drpicox.fishingLagoon.rounds;

import com.drpicox.fishingLagoon.bots.BotId;
import com.drpicox.fishingLagoon.common.TimeOffset;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.engine.*;
import com.drpicox.fishingLagoon.parser.RoundDescriptor;
import com.drpicox.fishingLagoon.rules.FishingLagoonRules;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Round {

    private RoundId id;
    private TimeStamp startTs;
    private TimeStamp endTs;
    private TimeStamp nowTs;
    private String state;
    private BotId selfId;
    private RoundDescriptor descriptor;
    private Map<BotId, RoundSeat> seats;
    private Map<BotId, RoundCommand> commands;
    private RoundScores scores;

    public Map<String,Object> toMap() {
        var result = new LinkedHashMap<String,Object>();
        result.put("id", id.getValue());
        result.put("startTs", startTs.getMilliseconds());
        result.put("endTs", endTs.getMilliseconds());
        if (nowTs != null) result.put("nowTs", nowTs.getMilliseconds());
        if (selfId != null) result.put("selfId", selfId.getValue());
        if (state != null) result.put("state", state);
        if (descriptor != null) result.put("descriptor", descriptor);
        if (seats != null) result.put("seats", seats);
        if (commands != null) result.put("commands", commands);
        if (scores != null) result.put("scores", scores);
        return result;
    }

    public Round(RoundId id, TimeStamp startTs, TimeOffset roundDuration) {
        this.id = id;
        this.startTs = startTs;
        this.endTs = startTs.plus(roundDuration);
    }

    public Round(RoundId id, TimeStamp startTs, TimeStamp endTs) {
        this.id = id;
        this.startTs = startTs;
        this.endTs = endTs;
    }

    public Round(Round round) {
        this.id = round.id;
        this.startTs = round.startTs;
        this.endTs = round.endTs;
    }

    public boolean isActive(TimeStamp ts) {
        return startTs.compareTo(ts) <= 0 && ts.compareTo(endTs) < 0;
    }

    public RoundId getId() {
        return id;
    }

    public TimeStamp getStartTs() {
        return startTs;
    }

    public TimeStamp getEndTs() {
        return endTs;
    }

    public TimeStamp getNowTs() {
        return nowTs;
    }

    public String getState() {
        return state;
    }

    public BotId getSelfId() {
        return selfId;
    }

    public RoundDescriptor getDescriptor() {
        return descriptor;
    }
    public Map<BotId, RoundSeat> getSeats() {
        return seats;
    }
    public Map<BotId, RoundCommand> getCommands() {
        return commands;
    }
    public RoundScores getScores() {
        return scores;
    }

    public void apply(RoundEngine roundEngine, FishingLagoonRules rules) {
        var timeState = roundEngine.getState();

        nowTs = roundEngine.getNowTs();
        state = timeState.toString();
        descriptor = null;
        seats = null;
        commands = null;
        scores = null;

        if (timeState.isDescriptorReadable()) {
            descriptor = roundEngine.getDescriptor();
        }
        if (timeState.isSeatsReadable()) {
            seats = roundEngine.getSeats().getSeats();
        }
        if (timeState.isCommandsReadable()) {
            commands = roundEngine.getCommands().getCommands();
        }
        if (timeState.isScoresReadable()) {
            scores = roundEngine.getScores(rules);
        }
    }

    public Round withSelfId(BotId id) {
        this.selfId = id;
        return this;
    }

}
