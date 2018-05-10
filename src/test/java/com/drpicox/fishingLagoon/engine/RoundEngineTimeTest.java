package com.drpicox.fishingLagoon.engine;


import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.parser.PropsParser;
import com.drpicox.fishingLagoon.parser.RoundParser;
import com.drpicox.fishingLagoon.rounds.RoundId;
import org.junit.Test;

import static com.drpicox.fishingLagoon.engine.RoundTimeState.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RoundEngineTimeTest {

    private RoundEngine createRound() {
        return parse(10L,"",
                "seatMilliseconds=20",
                "commandMilliseconds=30",
                "scoreMilliseconds=40");
    }

    @Test
    public void round_time_state_created() {
        var round = createRound();

        var state0 = roundAt(round, new TimeStamp(0L)).getState();
        var state9 = roundAt(round, new TimeStamp(9L)).getState();

        assertThat(state0, is(CREATED));
        assertThat(state9, is(CREATED));
        assertThat(CREATED.isAcceptingCommands(), is(false));
        assertThat(CREATED.isAcceptingSeats(), is(false));
        assertThat(CREATED.isCommandsReadable(), is(false));
        assertThat(CREATED.isDescriptorReadable(), is(false));
        assertThat(CREATED.isScoresReadable(), is(false));
        assertThat(CREATED.isSeatsReadable(), is(false));
    }

    @Test
    public void round_time_state_seating() {
        var round = createRound();

        var state10 = roundAt(round, new TimeStamp(10L)).getState();
        var state29 = roundAt(round, new TimeStamp(29L)).getState();

        assertThat(state10, is(SEATING));
        assertThat(state29, is(SEATING));
        assertThat(SEATING.isAcceptingCommands(), is(false));
        assertThat(SEATING.isAcceptingSeats(), is(true));
        assertThat(SEATING.isCommandsReadable(), is(false));
        assertThat(SEATING.isDescriptorReadable(), is(true));
        assertThat(SEATING.isScoresReadable(), is(false));
        assertThat(SEATING.isSeatsReadable(), is(true));
    }

    @Test
    public void round_time_state_commanding() {
        var round = createRound();

        var state30 = roundAt(round, new TimeStamp(30L)).getState();
        var state59 = roundAt(round, new TimeStamp(59L)).getState();

        assertThat(state30, is(COMMANDING));
        assertThat(state59, is(COMMANDING));
        assertThat(COMMANDING.isAcceptingCommands(), is(true));
        assertThat(COMMANDING.isAcceptingSeats(), is(false));
        assertThat(COMMANDING.isCommandsReadable(), is(false));
        assertThat(COMMANDING.isDescriptorReadable(), is(true));
        assertThat(COMMANDING.isScoresReadable(), is(false));
        assertThat(COMMANDING.isSeatsReadable(), is(true));
    }

    @Test
    public void round_time_state_scoring() {
        var round = createRound();

        var state60 = roundAt(round, new TimeStamp(60L)).getState();
        var state99 = roundAt(round, new TimeStamp(99L)).getState();

        assertThat(state60, is(SCORING));
        assertThat(state99, is(SCORING));
        assertThat(SCORING.isAcceptingCommands(), is(false));
        assertThat(SCORING.isAcceptingSeats(), is(false));
        assertThat(SCORING.isCommandsReadable(), is(true));
        assertThat(SCORING.isDescriptorReadable(), is(true));
        assertThat(SCORING.isScoresReadable(), is(true));
        assertThat(SCORING.isSeatsReadable(), is(true));
    }

    @Test
    public void round_time_state_finished() {
        var round = createRound();

        var state100 = roundAt(round, new TimeStamp(100L)).getState();
        var state5000 = roundAt(round, new TimeStamp(5000L)).getState();

        assertThat(state100, is(FINISHED));
        assertThat(state5000, is(FINISHED));
        assertThat(FINISHED.isAcceptingCommands(), is(false));
        assertThat(FINISHED.isAcceptingSeats(), is(false));
        assertThat(FINISHED.isCommandsReadable(), is(false));
        assertThat(FINISHED.isDescriptorReadable(), is(true));
        assertThat(FINISHED.isScoresReadable(), is(true));
        assertThat(FINISHED.isSeatsReadable(), is(false));
    }

    private RoundEngine roundAt(RoundEngine round, TimeStamp nowTs) {
        round.updateNow(nowTs);
        return round;
    }

    private static RoundEngine parse(long startTs, String... roundTextLines) {
        var roundText = String.join("\n", roundTextLines);
        var roundDescriptor = new RoundParser(new PropsParser()).parse(roundText);
        return new RoundEngine(new RoundId("r0"), new TimeStamp(startTs), roundDescriptor);
    }

    private static TimeStamp ts(long milliseconds) {
        return new TimeStamp(milliseconds);
    }
}
