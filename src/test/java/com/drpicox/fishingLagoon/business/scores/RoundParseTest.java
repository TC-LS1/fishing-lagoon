package com.drpicox.fishingLagoon.business.scores;


import com.drpicox.fishingLagoon.common.parser.PropsParser;
import com.drpicox.fishingLagoon.common.parser.RoundParser;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class RoundParseTest {

    @Test
    public void round_parse_test() {
        String tournamentText = String.join("\n", "",
                "maxDensity=3.0",
                "maxDensity=4.0",
                "lagoons=lagoonAverage",
                "weekCount=5",
                "lagoonAverage.fishPopulation=10"
        );

        var parser = new RoundParser(new PropsParser());
        var roundDescriptor = parser.parse(tournamentText);
        var lagoonDescriptor0 = roundDescriptor.getLagoonDescriptor(0);

        assertThat(roundDescriptor.getMaxDensity(), is(4.0));
        assertThat(roundDescriptor.getWeekCount(), is(5));
        assertThat(roundDescriptor.getSeatMilliseconds(), is(20000L));
        assertThat(roundDescriptor.getCommandMilliseconds(), is(20000L));
        assertThat(roundDescriptor.getScoreMilliseconds(), is(20000L));
        assertThat(lagoonDescriptor0.getFishPopulation(), is(10L));
    }

    @Test
    public void round_parse_defaults() {
        String tournamentText = "";

        var parser = new RoundParser(new PropsParser());
        var roundDescriptor = parser.parse(tournamentText);
        var lagoonDescriptor0 = roundDescriptor.getLagoonDescriptor(0);

        assertThat(roundDescriptor.getMaxDensity(), is(5.0));
        assertThat(roundDescriptor.getWeekCount(), is(10));
        assertThat(roundDescriptor.getSeatMilliseconds(), is(20000L));
        assertThat(roundDescriptor.getCommandMilliseconds(), is(20000L));
        assertThat(roundDescriptor.getScoreMilliseconds(), is(20000L));
        assertThat(lagoonDescriptor0.getFishPopulation(), is(0L));
    }

    @Test
    public void round_parse_without_defaults() {
        var tournamentText = String.join("\n", "",
                "seatMilliseconds=60000",
                "commandMilliseconds=30000",
                "scoreMilliseconds=25000",
                "maxDensity=5.2",
                "lagoons=lagoonSmall,lagoonLarge",
                "weekCount=5",
                "lagoonSmall.fishPopulation=5",
                "lagoonAverage.fishPopulation=10",
                "lagoonLarge.fishPopulation=50"
        );

        var parser = new RoundParser(new PropsParser());
        var roundDescriptor = parser.parse(tournamentText);

        var lagoonDescriptor0 = roundDescriptor.getLagoonDescriptor(0);
        var lagoonDescriptor1 = roundDescriptor.getLagoonDescriptor(1);


        assertThat(roundDescriptor.getMaxDensity(), is(5.2));
        assertThat(roundDescriptor.getWeekCount(), is(5));
        assertThat(roundDescriptor.getSeatMilliseconds(), is(60000L));
        assertThat(roundDescriptor.getCommandMilliseconds(), is(30000L));
        assertThat(roundDescriptor.getScoreMilliseconds(), is(25000L));
        assertThat(lagoonDescriptor0.getFishPopulation(), is(5L));
        assertThat(lagoonDescriptor1.getFishPopulation(), is(50L));
    }

}
