package com.drpicox.fishingLagoon.business.scores;


import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.common.parser.PropsParser;
import com.drpicox.fishingLagoon.common.parser.RoundParser;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import org.junit.Test;

import static com.drpicox.fishingLagoon.helpers.Helpers.bot;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RoundSeatingTest {

    private Round createRound(String... extraLines) {
        return parse("",
                "maxDensity=2.0",
                "lagoons=lagoonSmall,lagoonBig",
                String.join("\n", extraLines));
    }

    @Test
    public void round_seating_gives_initially_zero_lagoon() {
        var round = createRound();

        assertThat(round.countLagoons(), is(0));
    }

    @Test
    public void round_seating_seat() {
        var round = createRound();

        round.seatBot(bot(1), 0);

        assertThat(round.getSeat(bot(1)).getLagoonIndex(), is(0));
        assertThat(round.getSeat(bot(2)), is(nullValue()));
        assertThat(round.getBots(), containsInAnyOrder(bot(1)));
        assertThat(round.countLagoons(), is(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_seating_cannot_seat_beyond_lagoon_count_lagoons() {
        var round = createRound();

        round.seatBot(bot(1), 1);
    }

    @Test
    public void round_seating_expands_lagoon_count() {
        var round = createRound();

        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);

        assertThat(round.getSeat(bot(1)).getLagoonIndex(), is(0));
        assertThat(round.getSeat(bot(2)).getLagoonIndex(), is(0));
        assertThat(round.getBots(), containsInAnyOrder(bot(1), bot(2)));
        assertThat(round.countLagoons(), is(1));
    }

    @Test
    public void round_seating_allow_change_lagoon() {
        var round = createRound();

        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);
        round.seatBot(bot(3), 0);
        round.seatBot(bot(2), 1);

        assertThat(round.getSeat(bot(1)).getLagoonIndex(), is(0));
        assertThat(round.getSeat(bot(2)).getLagoonIndex(), is(1));
        assertThat(round.getBots(), containsInAnyOrder(bot(1), bot(2), bot(3)));
        assertThat(round.countLagoons(), is(2));
    }

    @Test
    public void round_seating_density_can_have_decimals() {
        var round = createRound("maxDensity=1.5");

        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);

        assertThat(round.countLagoons(), is(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_seating_throws_when_inexisting_lagoons() {
        var round = createRound();

        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 1);
    }

    private static Round parse(String... roundTextLines) {
        var roundText = String.join("\n", roundTextLines);
        var roundDescriptor = new RoundParser(new PropsParser()).parse(roundText);
        return new Round(new RoundId("r0"), new TimeStamp(0L), roundDescriptor);
    }

}
