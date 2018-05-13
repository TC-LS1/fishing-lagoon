package com.drpicox.fishingLagoon.business.rounds;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.common.actions.Actions;
import com.drpicox.fishingLagoon.common.actions.RestAction;

import java.util.List;
import java.util.Set;

public class Round {
    private RoundId id;
    private TimeStamp startTs;

    private RoundDescriptor descriptor;
    private RoundSeats seats;
    private RoundCommands commands;

    public Round(RoundId id, TimeStamp startTs, RoundDescriptor descriptor, RoundSeats seats, RoundCommands commands) {
        this.id = id;
        this.startTs = startTs;
        this.descriptor = descriptor;

        this.seats = seats;
        this.commands = commands;
    }

    public Round(RoundId id, TimeStamp startTs, RoundDescriptor descriptor) {
        this(id, startTs, descriptor, new RoundSeats(), new RoundCommands());
    }

    public RoundId getId() {
        return id;
    }

    public TimeStamp getStartTs() {
        return startTs;
    }
    public TimeStamp getEndTs() {
        return startTs.plus(descriptor.getFinishOffset());
    }
    public RoundTimeState getState(TimeStamp nowTs) {
        return RoundTimeState.get(nowTs.getOffsetFrom(startTs), descriptor);
    }

    public RoundDescriptor getDescriptor() {
        return descriptor;
    }

    public void seatBot(BotId bot, int lagoonIndex) {
        seats.seatBot(bot, lagoonIndex, countLagoons(bot));
    }
    public Set<BotId> getBots() {
        return seats.getBots();
    }
    public Set<BotId> getLagoonBots(int lagoonIndex) {
        return seats.getLagoonBots(lagoonIndex);
    }
    public RoundSeat getSeat(BotId bot) {
        return seats.get(bot);
    }
    public RoundSeats getSeats() {
        return seats;
    }

    public int countLagoons() {
        return countLagoons(false);
    }
    private int countLagoons(BotId botAssumedToBeSeated) {
        return countLagoons(!seats.isSeated(botAssumedToBeSeated));
    }
    private int countLagoons(boolean addOneToSeatsCount) {
        var maxDensity = descriptor.getMaxDensity();
        var seatsCount = seats.count();
        var plusOne = addOneToSeatsCount ? 1 : 0;
        var result = (int)Math.ceil((seatsCount + plusOne) / maxDensity);
        return result;
    }

    public void commandBot(BotId bot, List<Action> actions) {
        if (!seats.isSeated(bot)) throw new IllegalArgumentException("Bot is not seated");

        commands.commandBot(bot, actions, descriptor.getWeekCount());
    }
    public RoundCommand getCommand(BotId bot) {
        return commands.get(bot);
    }
    public Action getAction(BotId bot, int weekIndex) {
        var command = getCommand(bot);
        if (command == null) return RestAction.NOOP;

        return command.getAction(weekIndex);
    }
    public RoundCommands getCommands() {
        return commands;
    }
}