package k.core.util.github;

import com.google.gson.*;

import k.core.util.github.RateLimit.RateType;
import k.core.util.github.gitjson.GithubJsonParser;

public final class GitHub {
    public static final int LOWEST_JAVA_ALLOWED = 6;
    static {
        String jVersion = System.getProperty("java.version");
        String minor = jVersion.split("\\.")[1];
        int imin = Integer.parseInt(minor);
        if (imin < LOWEST_JAVA_ALLOWED) {
            throw new UnsupportedOperationException("no support under jre"
                    + imin);
        }
    }

    public static void authorize(String authScope) {
        // basic auth first
        GData response = GNet.postData("/authorizations",
                GNet.NO_HEADERS_SPECIFIED, postContent);
    }

    public static GData allRateLimits() {
        return GNet.getData("/rate_limit", GNet.NO_HEADERS_SPECIFIED);
    }

    public static RateLimit rateLimit(RateType search) {
        if (search == null) {
            throw new NullPointerException();
        }
        GData all = allRateLimits();
        if (search == RateType.CORE) {
            return all.rate(); // already done!
        } else if (search == RateType.SEARCH) {
            // get the search data
            GithubJsonParser searchData = GithubJsonParser.begin(all.getData())
                    .subparser("resources/search");
            // parse the values
            int remain = searchData.data("remaining").getAsInt();
            int limit = searchData.data("limit").getAsInt();
            long reset = searchData.data("reset").getAsLong();
            searchData.end();
            // return object!
            return new RateLimit(remain, limit, reset);
        }
        throw new IllegalArgumentException("Unknown RateType " + search);
    }
}
