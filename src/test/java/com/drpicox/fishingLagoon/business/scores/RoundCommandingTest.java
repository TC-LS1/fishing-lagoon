package com.drpicox.fishingLagoon.business.scores;


import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.common.parser.PropsParser;
import com.drpicox.fishingLagoon.common.parser.RoundParser;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import org.junit.Test;

import java.util.Arrays;

import static com.drpicox.fishingLagoon.common.actions.Actions.fish;
import static com.drpicox.fishingLagoon.helpers.Helpers.bot;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RoundCommandingTest {

    private Round createRound(String... extraLines) {
        return parse("",
                "maxDensity=2.0",
                "weekCount=2",
                "lagoons=lagoonSmall,lagoonBig",
                String.join("\n", extraLines));
    }

    @Test
    public void round_commanding_accepts_commands_from_seated_bots() {
        var round = createRound();

        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);

        round.commandBot(bot(1), Arrays.asList(fish(1), fish(2)));
        round.commandBot(bot(2), Arrays.asList(fish(3), fish(4)));

        assertThat(round.getAction(bot(1), 0), is(fish(1)));
        assertThat(round.getAction(bot(1), 1), is(fish(2)));
        assertThat(round.getAction(bot(2), 0), is(fish(3)));
        assertThat(round.getAction(bot(2), 1), is(fish(4)));
        assertThat(round.getBots(), containsInAnyOrder(bot(1), bot(2)));
        assertThat(round.countLagoons(), is(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_commanding_throws_commands_from_no_seated_bots() {
        var round = createRound();

        round.seatBot(bot(1), 0);
        round.commandBot(bot(1), Arrays.asList(fish(1), fish(2)));
        round.commandBot(bot(2), Arrays.asList(fish(3), fish(4)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_commanding_requires_exact_size_list() {
        var round = createRound();
        round.seatBot(bot(1), 0);

        round.commandBot(bot(1), Arrays.asList(fish(1), fish(2), fish(3)));
    }

    private static Round parse(String... roundTextLines) {
        var roundText = String.join("\n", roundTextLines);
        var roundDescriptor = new RoundParser(new PropsParser()).parse(roundText);
        var round = new Round(new RoundId("r0"), new TimeStamp(0L), roundDescriptor);
        return round;
    }


}
