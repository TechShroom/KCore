package k.core.util.github;

import java.util.Calendar;
import java.util.Date;

public final class RateLimit {
    private static final Calendar staticCal = Calendar.getInstance();

    public static enum RateType {
        CORE, SEARCH;
    }

    private final int remaining, limit;
    private final long reset;
    private final Date resetDate;

    RateLimit(int rateremain, int ratelim, long ratereset) {
        remaining = rateremain;
        limit = ratelim;
        reset = ratereset;
        resetDate = setCalAndGet(reset);
    }

    @Override
    public String toString() {
        return remaining + "/" + limit + ", resets at " + resetDate;
    }

    public int getRemaining() {
        return remaining;
    }

    public int getLimit() {
        return limit;
    }

    public long getResetTime() {
        return reset;
    }

    public Date getResetTimeAsDate() {
        return resetDate;
    }

    private static synchronized Date setCalAndGet(long millis) {
        staticCal.setTimeInMillis(millis);
        return staticCal.getTime();
    }
}
