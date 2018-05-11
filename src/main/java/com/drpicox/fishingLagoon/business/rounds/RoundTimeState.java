package com.drpicox.fishingLagoon.business.rounds;

import com.drpicox.fishingLagoon.common.TimeOffset;

public enum RoundTimeState {
    CREATED(""),
    SEATING("descriptor,seats,active", "seats"),
    COMMANDING("descriptor,seats,active", "commands"),
    SCORING("descriptor,seats,commands,scores,active"),
    FINISHED("descriptor,scores");

    private boolean isDescriptorReadable;
    private boolean isSeatsReadable;
    private boolean isCommandsReadable;
    private boolean isScoresReadable;
    private boolean isAcceptingSeats;
    private boolean isAcceptingCommands;
    private boolean isActive;

    public boolean isDescriptorReadable() {
        return isDescriptorReadable;
    }

    public boolean isSeatsReadable() {
        return isSeatsReadable;
    }

    public boolean isCommandsReadable() {
        return isCommandsReadable;
    }

    public boolean isScoresReadable() {
        return isScoresReadable;
    }

    public boolean isAcceptingSeats() {
        return isAcceptingSeats;
    }

    public boolean isAcceptingCommands() {
        return isAcceptingCommands;
    }

    public boolean isActive() {
        return isActive;
    }

    RoundTimeState(String readables) {
        this(readables, "");
    }

    RoundTimeState(String readables, String accepting) {
        isDescriptorReadable = readables.contains("descriptor");
        isSeatsReadable = readables.contains("seats");
        isCommandsReadable = readables.contains("commands");
        isScoresReadable = readables.contains("scores");
        isAcceptingSeats = accepting.equals("seats");
        isAcceptingCommands = accepting.equals("commands");
        isActive = readables.contains("active");
    }

    static RoundTimeState get(TimeOffset offset, RoundDescriptor roundDescriptor) {
        var milliseconds = offset.getMilliseconds();
        var seatMilliseconds = roundDescriptor.getSeatMilliseconds();
        var commandMilliseconds = roundDescriptor.getCommandMilliseconds();
        var scoreMilliseconds = roundDescriptor.getScoreMilliseconds();

        if (milliseconds < 0) return CREATED;
        if (milliseconds < seatMilliseconds) return SEATING;
        if (milliseconds < seatMilliseconds + commandMilliseconds) return COMMANDING;
        if (milliseconds < seatMilliseconds + commandMilliseconds + scoreMilliseconds) return SCORING;
        return FINISHED;
    }
}
