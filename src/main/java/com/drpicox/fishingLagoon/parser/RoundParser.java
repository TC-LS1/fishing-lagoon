package com.drpicox.fishingLagoon.parser;

import java.util.*;

public class RoundParser {

    private PropsParser propsParser;

    public RoundParser(PropsParser propsParser) {
        this.propsParser = propsParser;
    }

    public RoundDescriptor parse(String roundText) {
        Props props = propsParser.parse(roundText);
        State state = new State(props);

        return state.getRound();
    }

    public String stringify(RoundDescriptor round) {
        StringBuilder roundLagoons = new StringBuilder("lagoons");
        StringBuilder lagoons = new StringBuilder();
        String coma = "=";
        for (int lagoonIndex = 0; lagoonIndex < round.getLagoonCount(); lagoonIndex++) {
            String lagoonKey = "lagoon" + lagoonIndex;
            roundLagoons.append(coma).append(lagoonKey);
            coma = ",";
            var lagoonLines = stringify(round.getLagoonDescriptor(lagoonIndex)).split("\n");
            for (var lagoonLine: lagoonLines) {
                lagoons.append(lagoonKey).append(".").append(lagoonLine).append("\n");
            }
        }

        StringBuilder roundText = new StringBuilder();
        roundText.append("weekCount=").append(round.getWeekCount()).append("\n");
        roundText.append("maxDensity=").append(round.getMaxDensity()).append("\n");
        roundText.append("scoreMilliseconds=").append(round.getScoreMilliseconds()).append("\n");
        roundText.append("commandMilliseconds=").append(round.getCommandMilliseconds()).append("\n");
        roundText.append("seatMilliseconds=").append(round.getSeatMilliseconds()).append("\n");
        roundText.append(roundLagoons).append("\n").append(lagoons);
        return roundText.toString();
    }

    private String stringify(LagoonDescriptor lagoon) {
        return "fishPopulation=" + lagoon.getFishPopulation();
    }

    private static class State {
        final Props props;
        final Map<String, LagoonDescriptor> lagoonDescriptorMap = new HashMap<>();

        public State(Props props) {
            this.props = props;
        }

        public RoundDescriptor getRound() {
            Double maxDensity = props.getDouble("maxDensity", 5.0);
            Integer weekCount = props.getInteger("weekCount", 10);
            String[] lagoonNames = props.getCsv("lagoons", "");

            Long seatMilliseconds = props.getLong("seatMilliseconds", 20000L);
            Long commandMilliseconds = props.getLong("commandMilliseconds", 20000L);
            Long scoreMilliseconds = props.getLong("scoreMilliseconds", 20000L);

            if (maxDensity < 1.0) throw new IllegalArgumentException("maxDensity cannot be below 1.0");
            if (weekCount < 1) throw new IllegalArgumentException("weekCount cannot be below 1");
            if (weekCount > 100) throw new IllegalArgumentException("weekCount cannot be above 100");

            List<LagoonDescriptor> lagoonDescriptors = getLagoonDescriptors(lagoonNames);
            return new RoundDescriptor(seatMilliseconds, commandMilliseconds, scoreMilliseconds, maxDensity, lagoonDescriptors, weekCount);
        }

        private List<LagoonDescriptor> getLagoonDescriptors(String... lagoonNames) {
            List<LagoonDescriptor> lagoonDescriptors = new ArrayList<>();
            for (String lagoonName : lagoonNames) {
                LagoonDescriptor lagoonDescriptor = getLagoonDescriptor(lagoonName);
                lagoonDescriptors.add(lagoonDescriptor);
            }
            return lagoonDescriptors;
        }

        private LagoonDescriptor getLagoonDescriptor(String key) {
            LagoonDescriptor lagoonDescriptor = lagoonDescriptorMap.get(key);
            if (lagoonDescriptor != null) return lagoonDescriptor;

            Long fishPopulation = props.getLong(key + ".fishPopulation", 0L);
            if (fishPopulation != null) {
                lagoonDescriptor = new LagoonDescriptor(fishPopulation);
                lagoonDescriptorMap.put(key, lagoonDescriptor);
            }

            return lagoonDescriptor;
        }
    }
}
