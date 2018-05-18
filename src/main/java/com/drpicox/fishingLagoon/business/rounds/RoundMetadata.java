package com.drpicox.fishingLagoon.business.rounds;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.tournaments.TournamentId;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.common.actions.RestAction;

import java.util.List;
import java.util.Set;

public class RoundMetadata {
    private RoundId id;
    private TimeStamp startTs;
    private TimeStamp endTs;
    private TournamentId tournamentId;

    public RoundMetadata(RoundId id, TournamentId tournamentId, TimeStamp startTs, TimeStamp endTs) {
        this.id = id;
        this.startTs = startTs;
        this.endTs = endTs;
        this.tournamentId = tournamentId;
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
    public TournamentId getTournamentId() {
        return tournamentId;
    }
}