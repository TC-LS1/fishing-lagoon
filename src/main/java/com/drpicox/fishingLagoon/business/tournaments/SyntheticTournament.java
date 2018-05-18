package com.drpicox.fishingLagoon.business.tournaments;

import com.drpicox.fishingLagoon.business.rounds.RoundDescriptor;
import com.drpicox.fishingLagoon.business.tournaments.strategy.Strategy;
import com.drpicox.fishingLagoon.common.TimeStamp;

import java.util.List;

public class SyntheticTournament implements Tournament {

    Class<Strategy> counterpartClass;

    public SyntheticTournament() {

    }

    @Override
    public void verifyRoundCreation(RoundDescriptor descriptor, TimeStamp now) {
        throw new IllegalStateException("Current tournament does not allow to create bot defined rounds.");
    }

    @Override
    public void verifyTournamentRoundCreation(TournamentId tournamentId, List<RoundDescriptor> descriptors, TimeStamp now) {
    }
}
