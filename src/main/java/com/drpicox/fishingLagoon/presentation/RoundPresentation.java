package com.drpicox.fishingLagoon.presentation;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.rounds.*;
import com.drpicox.fishingLagoon.business.rules.FishingLagoonRules;
import com.drpicox.fishingLagoon.business.scores.RoundScores;
import com.drpicox.fishingLagoon.common.TimeStamp;

import java.util.Map;
import java.util.TreeMap;

public class RoundPresentation {

    private String id;
    private long startTs;
    private long endTs;
    private long nowTs;
    private String state;
    private String selfId;
    private RoundDescriptor descriptor;
    private Map<String, RoundSeat> seats;
    private Map<String, RoundCommand> commands;
    private RoundScores scores;

    public static RoundPresentation from(Round round) {
        if (round == null) return null;
        return new RoundPresentation(round);
    }

    public static RoundPresentation from(Round round, TimeStamp nowTs, BotId selfId, FishingLagoonRules rules) {
        if (round == null) return null;
        return new RoundPresentation(round, nowTs, selfId, rules);
    }

    private RoundPresentation(Round round) {
        this.id = round.getId().getValue();
        this.startTs = round.getStartTs().getMilliseconds();
        this.endTs = round.getEndTs().getMilliseconds();
    }

    private RoundPresentation(Round round, TimeStamp nowTs, BotId selfId, FishingLagoonRules rules) {
        this(round);

        this.nowTs = nowTs.getMilliseconds();
        this.selfId = selfId != null ? selfId.getValue() : null;

        var state = round.getState(nowTs);
        this.state = state.name();
        if (state.isDescriptorReadable()) {
            this.descriptor = round.getDescriptor();
        }
        if (state.isSeatsReadable()) {
            this.seats = new TreeMap<>();
            for (var bot: round.getBots()) {
                var seat = round.getSeat(bot);
                this.seats.put(bot.getValue(), seat);
            }
        }
        if (state.isCommandsReadable()) {
            this.commands = new TreeMap<>();
            for (var bot: round.getBots()) {
                var command = round.getCommand(bot);
                this.commands.put(bot.getValue(), command);
            }
        }
        if (state.isScoresReadable()) {
            this.scores = rules.score(round);
        }
    }

    public RoundId getId() {
        return new RoundId(id);
    }

    public BotId getSelfId() {
        return new BotId(selfId);
    }
}
