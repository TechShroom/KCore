package k.core.util.github;

import java.util.Calendar;
import java.util.Date;

import k.core.util.github.gitjson.GithubJsonCreator;
import k.core.util.github.gitjson.GithubJsonParser;

import com.google.gson.JsonPrimitive;

public final class RateLimit {
    public static enum RateType {
        CORE, SEARCH;
    }

    private static final Calendar staticCal = Calendar.getInstance();

    private static synchronized Date setCalAndGet(long millis) {
        staticCal.setTimeInMillis(millis);
        return staticCal.getTime();
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

    public int getLimit() {
        return limit;
    }

    public int getRemaining() {
        return remaining;
    }

    public long getResetTime() {
        return reset;
    }

    public Date getResetTimeAsDate() {
        return resetDate;
    }

    public String json() {
        return GithubJsonCreator.getForObjectCreation()
                .add("limit", new JsonPrimitive(limit))
                .add("reset", new JsonPrimitive(reset))
                .add("remaining", new JsonPrimitive(remaining)).toString();
    }

    public static RateLimit fromJSON(String json) {
        GithubJsonParser parser = GithubJsonParser.begin(json);
        JsonPrimitive lim = parser.data("limit").getAsJsonPrimitive();
        JsonPrimitive res = parser.data("reset").getAsJsonPrimitive();
        JsonPrimitive rem = parser.data("remaining").getAsJsonPrimitive();
        parser.end();
        return new RateLimit(rem.getAsInt(), lim.getAsInt(), res.getAsLong());
    }

    @Override
    public String toString() {
        return remaining + "/" + limit + ", resets at " + resetDate;
    }
}
