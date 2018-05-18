package com.drpicox.fishingLagoon.business.tournaments;

import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.business.rounds.RoundDescriptor;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.presentation.GamePresentation;
import org.junit.Before;

import java.sql.SQLException;
import java.util.List;

public class SparringTournament implements Tournament {

    @Override
    public void verifyRoundCreation(RoundDescriptor descriptor, TimeStamp now) {
    }

    @Override
    public void verifyTournamentRoundCreation(TournamentId tournamentId, List<RoundDescriptor> descriptors, TimeStamp now) {
        throw new IllegalStateException("Sparring tournaments does not accept tournament rounds");
    }
}
