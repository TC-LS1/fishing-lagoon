package com.drpicox.fishingLagoon.business.tournaments;

import com.drpicox.fishingLagoon.business.rounds.RoundDescriptor;
import com.drpicox.fishingLagoon.common.TimeStamp;

import java.util.List;

public interface Tournament {
    void verifyRoundCreation(RoundDescriptor descriptor, TimeStamp now);
    void verifyTournamentRoundCreation(TournamentId tournamentId, List<RoundDescriptor> descriptors, TimeStamp now);
}
