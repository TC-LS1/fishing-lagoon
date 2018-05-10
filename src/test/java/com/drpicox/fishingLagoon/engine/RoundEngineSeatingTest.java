package com.drpicox.fishingLagoon.engine;


import com.drpicox.fishingLagoon.bots.BotId;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.parser.PropsParser;
import com.drpicox.fishingLagoon.parser.RoundParser;
import com.drpicox.fishingLagoon.rounds.RoundId;
import org.junit.Test;

import static com.drpicox.fishingLagoon.engine.RoundTestHelper.timeForSeat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RoundEngineSeatingTest {

    private RoundEngine createRound(String... extraLines) {
        return parse("",
                "maxDensity=2.0",
                "lagoons=lagoonSmall,lagoonBig",
                String.join("\n", extraLines));
    }

    @Test
    public void round_seating_gives_initially_zero_lagoon() {
        var round = createRound();

        assertThat(round.getLagoonCount(), is(0));
    }

    @Test
    public void round_seating_seat() {
        var round = createRound();

        timeForSeat(round);
        round.seatBot(bot(1), 0);

        var seats = round.getSeats();
        assertThat(seats.getBotSeat(bot(1)), is(0));
        assertThat(seats.getBotSeat(bot(2)), is(-1));
        assertThat(seats.getBotCount(), is(1));
        assertThat(seats.getBots(), containsInAnyOrder(bot(1)));
        assertThat(round.getLagoonCount(), is(1));
    }

    @Test
    public void round_seating_cannot_seat_beyond_lagoon_count_lagoons() {
        var round = createRound();

        timeForSeat(round);
        round.seatBot(bot(1), 1);

        var seats = round.getSeats();
        assertThat(seats.getBotSeat(bot(1)), is(-1));
        assertThat(seats.getBotCount(), is(0));
        assertThat(seats.getBots(), is(empty()));
        assertThat(round.getLagoonCount(), is(0));
    }

    @Test
    public void round_seating_expands_lagoon_count() {
        var round = createRound();

        timeForSeat(round);
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);

        var seats = round.getSeats();
        assertThat(seats.getBotSeat(bot(1)), is(0));
        assertThat(seats.getBotSeat(bot(2)), is(0));
        assertThat(seats.getBotCount(), is(2));
        assertThat(seats.getBots(), containsInAnyOrder(bot(1), bot(2)));
        assertThat(round.getLagoonCount(), is(1));
    }

    @Test
    public void round_seating_allow_change_lagoon() {
        var round = createRound();

        timeForSeat(round);
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);
        round.seatBot(bot(3), 0);
        round.seatBot(bot(2), 1);

        var seats = round.getSeats();
        assertThat(seats.getBotSeat(bot(1)), is(0));
        assertThat(seats.getBotSeat(bot(2)), is(1));
        assertThat(seats.getBotCount(), is(3));
        assertThat(seats.getBots(), containsInAnyOrder(bot(1), bot(2), bot(3)));
        assertThat(round.getLagoonCount(), is(2));
    }

    @Test
    public void round_seating_density_can_have_decimals() {
        var round = createRound("maxDensity=1.5");

        timeForSeat(round);
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);

        var seats = round.getSeats();
        assertThat(round.getLagoonCount(), is(2));
    }

    @Test
    public void round_seating_ignores_inexisting_lagoons() {
        var round = createRound();

        timeForSeat(round);
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 1);

        var seats = round.getSeats();
        assertThat(seats.getBotSeat(bot(1)), is(0));
        assertThat(seats.getBotSeat(bot(2)), is(-1));
        assertThat(seats.getBotCount(), is(1));
        assertThat(seats.getBots(), containsInAnyOrder(bot(1)));
        assertThat(round.getLagoonCount(), is(1));
    }

    private static RoundEngine parse(String... roundTextLines) {
        var roundText = String.join("\n", roundTextLines);
        var roundDescriptor = new RoundParser(new PropsParser()).parse(roundText);
        return new RoundEngine(new RoundId("r0"), new TimeStamp(0L), roundDescriptor);
    }

    private static BotId bot(int n) {
        return new BotId("bot" + n);
    }

}
