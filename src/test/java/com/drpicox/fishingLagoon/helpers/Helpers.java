package com.drpicox.fishingLagoon.helpers;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.business.tournaments.TournamentId;
import com.drpicox.fishingLagoon.common.TimeOffset;
import com.drpicox.fishingLagoon.common.TimeStamp;

public class Helpers {

    public static final long SECOND = 1000L;
    public static final long MINUTE = 60 * SECOND;

    public static final long SEAT_MILLISECONDS = 20 * SECOND;
    public static final long COMMAND_MILLISECONDS = 20 * SECOND;
    public static final long SCORE_MILLISECONDS = 20 * SECOND;
    public static final long TOTAL_MILLISECONDS = SEAT_MILLISECONDS + COMMAND_MILLISECONDS + SCORE_MILLISECONDS;

    public static BotToken token(int n) {
        return new BotToken("token" + n);
    }
    public static BotId bot(int n) {
        return new BotId("bot" + n);
    }
    public static RoundId round(int n) {
        return new RoundId("round" + n);
    }
    public static TournamentId tournament() {
        return new TournamentId("SPARRING");
    }
    public static TournamentId tournament(String n) {
        return new TournamentId(n);
    }

    public static TimeStamp ts(long n) {
        return new TimeStamp(n);
    }
    public static TimeOffset to(long n) {
        return new TimeOffset(n);
    }

}
