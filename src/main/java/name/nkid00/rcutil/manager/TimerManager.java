package name.nkid00.rcutil.manager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import name.nkid00.rcutil.helper.GametimeHelper;
import name.nkid00.rcutil.model.Clock;
import name.nkid00.rcutil.model.Timer;

public class TimerManager {
    private static SetMultimap<Long, Timer> gametickStartTimers = Multimaps
            .synchronizedSetMultimap(HashMultimap.create());
    private static SetMultimap<Long, Timer> gametickEndTimers = Multimaps
            .synchronizedSetMultimap(HashMultimap.create());

    public static void register(Timer timer) {
        switch (timer.event().name()) {
            case "onGametickStartDelay":
            case "onGametickStartClock":
                gametickStartTimers.put(timer.targetTime(), timer);
                return;
            case "onGametickEndDelay":
            case "onGametickEndClock":
                gametickEndTimers.put(timer.targetTime(), timer);
                return;
        }
    }

    public static void deregister(Timer timer) {
        switch (timer.event().name()) {
            case "onGametickStartDelay":
            case "onGametickStartClock":
                gametickStartTimers.remove(timer.targetTime(), timer);
                return;
            case "onGametickEndDelay":
            case "onGametickEndClock":
                gametickEndTimers.remove(timer.targetTime(), timer);
                return;
        }
    }

    public static ImmutableSet<Timer> onGametickStart() {
        var gametime = GametimeHelper.gametime();
        if (!gametickStartTimers.containsKey(gametime)) {
            return ImmutableSet.of();
        }
        var timers = gametickStartTimers.get(gametime);
        var timersCopy = ImmutableSet.copyOf(timers);
        for (Timer timer : timersCopy) {
            timers.remove(timer);
            if (timer instanceof Clock) {
                timer.reset();
                gametickStartTimers.put(timer.targetTime(), timer);
            }
        }
        return timersCopy;
    }

    public static ImmutableSet<Timer> onGametickEnd() {
        var gametime = GametimeHelper.gametime();
        if (!gametickEndTimers.containsKey(gametime)) {
            return ImmutableSet.of();
        }
        var timers = gametickEndTimers.get(gametime);
        var timersCopy = ImmutableSet.copyOf(timers);
        for (Timer timer : timersCopy) {
            timers.remove(timer);
            if (timer instanceof Clock) {
                timer.reset();
                gametickEndTimers.put(timer.targetTime(), timer);
            }
        }
        return timersCopy;
    }
}
