package com.drpicox.fishingLagoon.engine;

import com.drpicox.fishingLagoon.common.TimeOffset;
import com.drpicox.fishingLagoon.parser.RoundDescriptor;

public class RoundTestHelper {

    public static void timeForSeat(RoundEngine round) {
        var startTs = round.getStartTs();
        var descriptor = round.getDescriptor();
        var seatOffset = descriptor.getSeatOffset();
        round.updateNow(startTs.plus(seatOffset));
    }

    public static void setTimeForCommand(RoundEngine round) {
        var startTs = round.getStartTs();
        var descriptor = round.getDescriptor();
        var commandOffset = descriptor.getCommandOffset();
        round.updateNow(startTs.plus(commandOffset));
    }

    public static void timeForScore(RoundEngine round) {
        var startTs = round.getStartTs();
        var descriptor = round.getDescriptor();
        var seatOffset = descriptor.getCommandOffset();
        round.updateNow(startTs.plus(seatOffset));
    }

}
