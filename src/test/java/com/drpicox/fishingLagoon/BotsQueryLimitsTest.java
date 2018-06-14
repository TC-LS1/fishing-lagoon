package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.presentation.limits.BotsQueryLimits;

import org.junit.Before;
import org.junit.Test;

import static com.drpicox.fishingLagoon.helpers.Helpers.*;
import static com.drpicox.fishingLagoon.presentation.limits.BotQueryLimit.TIME_LIMIT;
import static com.drpicox.fishingLagoon.presentation.limits.BotQueryLimit.TIME_PENALISATION;

public class BotsQueryLimitsTest {

    private static final long LIMIT = TIME_LIMIT;
    private static final long PENALIZATION = TIME_PENALISATION;

    private BotsQueryLimits limits;

    @Before
    public void instance_limits() {
        limits = new BotsQueryLimits();
    }

    private void manyAccesses(int times, BotToken token, TimeStamp ts) {
        for (var i = 0; i < times; i++) {
            limits.trackAccess(token, ts);
        }
    }

    private void makeIllegalAccess(BotToken token, TimeStamp ts) {
        try {
            limits.trackAccess(token(1), ts(1 * LIMIT - 1));

            throw new AssertionError("Illegal trackAccess it turned into a legal one unexpectedly");

        } catch (IllegalStateException expectedException) {}
    }

    @Test
    public void tracks_accesses_by_bot_token() {
        limits.trackAccess(token(1), ts(0L));
        limits.trackAccess(token(2), ts(0L));
        limits.trackAccess(token(3), ts(0L));
    }

    @Test
    public void allows_to_access_5_times_in_the_same_second() {
        manyAccesses(5, token(1), ts(0L));
    }

    @Test
    public void allows_to_access_5_times_in_the_same_second_and_5_more_in_next_second() {
        manyAccesses(5, token(1), ts(0 * LIMIT));
        manyAccesses(5, token(1), ts(1 * LIMIT));
    }

    @Test(expected = IllegalStateException.class)
    public void throws_exception_if_there_are_more_than_5_access_in_the_same_second() {
        manyAccesses(5, token(1), ts(0 * LIMIT));

        limits.trackAccess(token(1), ts(1 * LIMIT - 1));
    }

    @Test(expected = IllegalStateException.class)
    public void throws_exception_if_there_are_more_than_5_access_in_the_same_second_alternative() {
        limits.trackAccess(token(1), ts(0 * LIMIT));
        manyAccesses(4, token(1), ts(1 * LIMIT - 2));

        limits.trackAccess(token(1), ts(1 * LIMIT - 1));
    }

    @Test
    public void once_limit_exeeded_it_allows_to_access_one_minute_later_after_the_first_illegal_access() {
        manyAccesses(5, token(1), ts(0 * LIMIT));
        makeIllegalAccess(token(1), ts(0 * LIMIT));

        limits.trackAccess(token(1), ts(1 * PENALIZATION));
    }

    @Test(expected = IllegalStateException.class)
    public void throws_limit_exeeded_until_just_the_minute_is_saisfied() {
        manyAccesses(5, token(1), ts(0 * LIMIT));
        makeIllegalAccess(token(1), ts(0 * LIMIT));

        limits.trackAccess(token(1), ts(1 * PENALIZATION - 1));
    }

    @Test
    public void once_limit_exeeded_further_accesses_do_not_penalise() {
        manyAccesses(5, token(1), ts(0 * LIMIT));
        makeIllegalAccess(token(1), ts(1 * LIMIT - 1));
        makeIllegalAccess(token(1), ts(1 * PENALIZATION - 1));

        limits.trackAccess(token(1), ts(1 * PENALIZATION));
    }

    @Test
    public void it_has_a_fast_check_that_do_not_fails_if_legal() {
        limits.verifyAccess(token(1), ts(0 * LIMIT));
    }

    @Test(expected = IllegalStateException.class)
    public void it_has_a_fast_check_that_fails_if_illegal() {
        manyAccesses(5, token(1), ts(0 * LIMIT));
        limits.verifyAccess(token(1), ts(0 * LIMIT));
    }

    @Test(expected = IllegalStateException.class)
    public void it_has_a_fast_check_that_fails_if_in_penalisation_time() {
        manyAccesses(5, token(1), ts(0 * LIMIT));
        makeIllegalAccess(token(1), ts(0 * LIMIT));

        limits.verifyAccess(token(1), ts(1 * PENALIZATION - 1));
    }

    @Test()
    public void it_has_a_fast_check_that_fails_if_in_penalisation_time_other_works() {
        manyAccesses(5, token(1), ts(0 * LIMIT));
        makeIllegalAccess(token(1), ts(0 * LIMIT));

        limits.verifyAccess(token(2), ts(1 * PENALIZATION - 1));
    }

    @Test
    public void it_has_a_fast_check_that_does_not_track_an_access() {
        manyAccesses(4, token(1), ts(0 * LIMIT));
        limits.verifyAccess(token(1), ts(0 * LIMIT));
        limits.trackAccess(token(1), ts(0 * LIMIT));
    }

    @Test
    public void regression_multiple_tokens_can_execute_simultanously() {
        manyAccesses(5, token(1), ts(0 * LIMIT));
        manyAccesses(5, token(2), ts(0 * LIMIT));
        manyAccesses(5, token(3), ts(0 * LIMIT));
    }

}
