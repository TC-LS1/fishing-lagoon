package com.drpicox.fishingLagoon.business.scores;


import com.drpicox.fishingLagoon.common.parser.PropsParser;
import com.drpicox.fishingLagoon.common.parser.RoundParser;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class RoundParseTest {

    @Test
    public void round_parse_test() {
        String roundText = String.join("\n", "",
                "maxDensity=3.0",
                "maxDensity=4.0",
                "lagoons=lagoonAverage",
                "weekCount=5",
                "lagoonAverage.fishPopulation=10"
        );

        var parser = new RoundParser(new PropsParser());
        var roundDescriptor = parser.parse(roundText);
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
        String roundText = "";

        var parser = new RoundParser(new PropsParser());
        var roundDescriptor = parser.parse(roundText);
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
        var roundText = String.join("\n", "",
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
        var roundDescriptor = parser.parse(roundText);

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

    @Test
    public void parse_multiple_rounds() {
        var tournamentText = String.join("\n", "",
                "weekCount=4",
                "---",
                "weekCount=5");

        var parser = new RoundParser(new PropsParser());
        var roundDescriptors = parser.parseRounds(tournamentText);

        assertThat(roundDescriptors, contains(
                hasProperty("weekCount", is(4)),
                hasProperty("weekCount", is(5))));
    }

    @Test
    public void parse_multiple_rounds_of_an_empty_string_creates_a_tournament_of_a_single_default_round() {
        var tournamentText = "";

        var parser = new RoundParser(new PropsParser());
        var roundDescriptors = parser.parseRounds(tournamentText);

        assertThat(roundDescriptors, contains(hasProperty("weekCount", is(10))));
    }

}
