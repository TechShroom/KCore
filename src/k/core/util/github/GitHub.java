package k.core.util.github;

import java.util.HashMap;

import k.core.util.github.RateLimit.RateType;
import k.core.util.github.gitjson.GithubJsonCreator;
import k.core.util.github.gitjson.GithubJsonParser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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

    public static GData allRateLimits() {
        return GNet.getData("/rate_limit", GNet.NO_HEADERS_SPECIFIED);
    }

    public static GAuth authorize(JsonArray authScope, String clientID,
            String clientSecret, String user, String pass, String... notes) {
        String note = "", note_url = "";
        if (notes != null) {
            if (notes.length >= 2) {
                note_url = notes[1];
            }
            if (notes.length >= 1) {
                note = notes[0];
            }
        }
        System.err.println("Authorizing...");
        JsonElement je = GithubJsonCreator.getForObjectCreation()
                .add("scope", authScope).add("note", note)
                .add("note_url", note_url).add("client_id", clientID)
                .add("client_secret", clientSecret).result();
        // basic auth first
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", GAuth.basic(user, pass));
        GData response = GNet.postData("/authorizations", headers,
                je.toString());
        GNet.authorization = new GAuth(GAuth.token(GithubJsonParser
                .begin(response.getData()).data("token").toString()),
                response.getFirstHeaderValue("Location"));
        System.err.println("Authorized: " + GNet.authorization);
        return GNet.authorization;
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
