package com.drpicox.fishingLagoon.engine;


import com.drpicox.fishingLagoon.bots.BotId;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.parser.PropsParser;
import com.drpicox.fishingLagoon.parser.RoundParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class RoundEngineCreatedTest {

    private RoundParser roundParser;

    @Before
    public void prepare() {
        roundParser = new RoundParser(new PropsParser());
    }

    @Test
    public void round_created_gets_description() {
        var round = parse("",
                "maxDensity=4.0",
                "weekCount=10",
                "lagoons=lagoonSmall,lagoonBig",
                "lagoonSmall.fishPopulation=5",
                "lagoonBig.fishPopulation=100");

        var roundDescriptor = round.getDescriptor();

        assertThat(roundDescriptor.getMaxDensity(), is(4.0));
        assertThat(roundDescriptor.getWeekCount(), is(10));
        assertThat(roundDescriptor.getLagoonDescriptor(0).getFishPopulation(), is(5L));
        assertThat(roundDescriptor.getLagoonDescriptor(1).getFishPopulation(), is(100L));
        assertThat(roundDescriptor.getLagoonDescriptor(2).getFishPopulation(), is(5L));
    }

    @Test
    public void round_created_descriptor_gets_text() {
        var round0 = parse("",
                "maxDensity=4.0",
                "weekCount=10",
                "seatMilliseconds=20",
                "commandMilliseconds=30",
                "scoreMilliseconds=40",
                "lagoons=lagoonSmall,lagoonBig",
                "lagoonSmall.fishPopulation=5",
                "lagoonBig.fishPopulation=100");

        var roundText0 = roundParser.stringify(round0.getDescriptor());
        var round1 = parse(roundText0);
        var roundText1 = roundParser.stringify(round1.getDescriptor());

        assertThat(roundText1, containsString("maxDensity=4"));
        assertThat(roundText1, containsString("weekCount=10"));
        assertThat(roundText1, containsString("seatMilliseconds=20"));
        assertThat(roundText1, containsString("commandMilliseconds=30"));
        assertThat(roundText1, containsString("scoreMilliseconds=40"));
        assertThat(roundText1, containsString("lagoons=lagoon0,lagoon1"));
        assertThat(roundText1, containsString("lagoon0.fishPopulation=5"));
        assertThat(roundText1, containsString("lagoon1.fishPopulation=100"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_created_cannot_have_less_than_density_1() {
        parse("maxDensity=0.99");
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_weeks_cannot_be_less_than_1() {
        parse("weekCount=0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_weeks_cannot_be_more_than_100() {
        parse("weekCount=101");
    }

    private static RoundEngine parse(String... roundTextLines) {
        var roundText = String.join("\n", roundTextLines);
        var roundDescriptor = new RoundParser(new PropsParser()).parse(roundText);
        return new RoundEngine(new TimeStamp(0L), roundDescriptor);
    }

    private static BotId bot(int id) {
        return new BotId("bot" + id);
    }

}
