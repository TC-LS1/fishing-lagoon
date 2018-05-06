package com.drpicox.fishingLagoon.engine;


import com.drpicox.fishingLagoon.bots.BotId;
import com.drpicox.fishingLagoon.parser.PropsParser;
import com.drpicox.fishingLagoon.parser.RoundParser;
import org.junit.Test;

import java.util.Arrays;

import static com.drpicox.fishingLagoon.actions.Actions.fish;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RoundEngineCommandingTest {

    private RoundEngine createRound(String... extraLines) {
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

        var commands = round.getCommands();
        assertThat(commands.getActions(bot(1)), contains(fish(1), fish(2)));
        assertThat(commands.getActions(bot(2)), contains(fish(3), fish(4)));
        assertThat(commands.getAction(bot(1), 0), is(fish(1)));
        assertThat(commands.getAction(bot(1), 1), is(fish(2)));
        assertThat(commands.getAction(bot(2), 0), is(fish(3)));
        assertThat(commands.getAction(bot(2), 1), is(fish(4)));
        assertThat(commands.getBotsCount(), is(2));
        assertThat(commands.getBots(), containsInAnyOrder(bot(1), bot(2)));
        assertThat(round.getLagoonCount(), is(1));
    }

    @Test
    public void round_commanding_ignores_commands_from_no_seated_bots() {
        var round = createRound();

        round.seatBot(bot(1), 0);
        round.commandBot(bot(1), Arrays.asList(fish(1), fish(2)));
        round.commandBot(bot(2), Arrays.asList(fish(3), fish(4)));

        var commands = round.getCommands();
        assertThat(commands.getActions(bot(1)), contains(fish(1), fish(2)));
        assertThat(commands.getBotsCount(), is(1));
        assertThat(commands.getBots(), containsInAnyOrder(bot(1)));
    }

    @Test(expected = IllegalStateException.class)
    public void round_commanding_before_seat_throws() {
        var round = createRound();

        round.seatBot(bot(1), 0);
        round.commandBot(bot(1), Arrays.asList(fish(1), fish(2)));

        round.seatBot(bot(2), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_commanding_requires_exact_size_list() {
        var round = createRound();
        round.seatBot(bot(1), 0);

        round.commandBot(bot(1), Arrays.asList(fish(1), fish(2), fish(3)));
    }

    private static RoundEngine parse(String... roundTextLines) {
        var roundText = String.join("\n", roundTextLines);
        var roundDescriptor = new RoundParser(new PropsParser()).parse(roundText);
        return new RoundEngine(0L, roundDescriptor);
    }

    private static BotId bot(int n) {
        return new BotId("bot" + n);
    }


}
