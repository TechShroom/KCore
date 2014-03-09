package k.core.util.github;

import java.util.HashMap;

import k.core.util.github.GNet.Auth;
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

    private static JsonArray scope;
    private static String cid, cs, u, p;
    private static String[] notes;

    public static GData allRateLimits() {
        return GNet.getData("/rate_limit", GNet.NO_HEADERS_SPECIFIED, Auth.TRY);
    }

    public static GAuth authorize(JsonArray authScope, String clientID,
            String clientSecret, String user, String pass, String... notes) {
        scope = authScope;
        cid = clientID;
        cs = clientSecret;
        u = user;
        p = pass;
        GitHub.notes = notes;
        if (GNet.authorization != null) {
            System.err.println("Using loaded token.");
            return GNet.authorization;
        }
        return authWithVars();
    }

    static GAuth authWithVars() {
        // remove current auth, might be invalid
        GNet.authorization = null;
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
                .add("scope", scope).add("note", note)
                .add("note_url", note_url).add("client_id", cid)
                .add("client_secret", cs).result();
        // basic auth first
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", GAuth.basic(u, p));
        GData response = GNet.postData("/authorizations", headers,
                je.toString(), Auth.OFF);
        GNet.authorization = new GAuth(GAuth.token(GithubJsonParser
                .begin(response.getData()).data("token").toString()),
                response.getFirstHeaderValue("Location"));
        System.err.println("Authorized: " + GNet.authorization);
        sync();
        return GNet.authorization;
    }

    public static void sync() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                GStore.storeGitData();
            }
        };
        Thread t = new Thread(r, "Data Sync");
        t.setDaemon(false);
        // right below max of 10
        t.setPriority(8);
        t.start();
    }

    public static void load() {
        GStore.loadGitData();
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

    public static GUser user(String username) {
        return GUser.from(GNet.getData("/users/" + username,
                GNet.NO_HEADERS_SPECIFIED, Auth.OFF));
    }
}
