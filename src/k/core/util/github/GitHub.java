package k.core.util.github;

import java.util.*;

import k.core.util.github.RateLimit.RateType;
import k.core.util.github.gitjson.GitHubJsonParser;
import k.core.util.github.gitjson.GithubJsonCreator;

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

    static JsonArray scope;
    private static String cid, cs, u, p;
    private static String[] notes;

    public static GData allLimitsFor(Auth auth) {
        return GNet.getData("/rate_limit", GNet.NO_HEADERS_SPECIFIED, auth);
    }

    public static GAuth authorize(JsonArray authScope, String clientID,
            String clientSecret, String user, String pass, String... notes) {
        cid = clientID;
        cs = clientSecret;
        u = user;
        p = pass;
        GitHub.notes = notes;
        if (GNet.authorization != null && scope.equals(authScope)) {
            System.err.println("Using loaded token.");
            scope = authScope;
            return GNet.authorization;
        }
        scope = authScope;
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
        JsonElement authData = GithubJsonCreator.getForObjectCreation()
                .add("scopes", scope).add("note", note)
                .add("note_url", note_url).add("client_id", cid)
                .add("client_secret", cs).result();
        // basic auth first
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", GAuth.basic(u, p));
        GData response = GNet.postData("/authorizations", headers,
                authData.toString(), Auth.OFF);
        System.err.println(response);
        GNet.authorization = new GAuth(GAuth.token(GitHubJsonParser
                .begin(response.getData()).data("token").toString()),
                response.getFirstHeaderValue("Location"));
        System.err.println("Authorized: " + GNet.authorization);
        sync();
        return GNet.authorization;
    }

    public static RateLimit limitsFor(RateType rate) {
        return limitsFor(rate, Auth.TRY);
    }

    public static RateLimit limitsFor(RateType rate, Auth auth) {
        if (rate == null) {
            throw new NullPointerException();
        }
        GData all = allLimitsFor(auth);
        if (rate == RateType.CORE) {
            return all.rate(); // already done!
        } else if (rate == RateType.SEARCH) {
            // get the search data
            GitHubJsonParser searchData = GitHubJsonParser.begin(all.getData())
                    .subparser("resources/search");
            // parse the values
            int remain = searchData.data("remaining").getAsInt();
            int limit = searchData.data("limit").getAsInt();
            long reset = searchData.data("reset").getAsLong();
            searchData.end();
            // return object!
            return new RateLimit(remain, limit, reset);
        }
        throw new IllegalArgumentException("Unknown RateType " + rate);
    }

    public static GData limitsFromMaxLimit() {
        return allLimitsFor(Auth.TRY);
    }

    public static void load() {
        GStore.loadGitData();
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

    public static GUser user(String username) {
        return user(username, false);
    }

    public static GUser user(String username, boolean auth) {
        return GUser.fromUrl(GNet.getData("/users/" + username,
                GNet.NO_HEADERS_SPECIFIED, (auth ? Auth.ON : Auth.OFF)));
    }

    public static List<GUser> users(int start, int end) {
        int since = start;
        boolean done = false;
        List<GUser> users = new ArrayList<GUser>();
        while (!done) {
            GData data = GNet.getData("/users?since=" + since,
                    GNet.NO_HEADERS_SPECIFIED, Auth.TRY);
            JsonArray array = GitHubJsonParser.parser.parse(data.getData())
                    .getAsJsonArray();
            List<GUser> augment = new ArrayList<GUser>(end - start);
            for (JsonElement je : array) {
                GUser u = GUser.fromUrl(je.toString());
                int id = je.getAsJsonObject().get("id").getAsInt();
                if (id == end) {
                    done = true;
                } else if (id > end) {
                    done = true;
                    break;
                }
                since = id;
                augment.add(u);
                if (done) {
                    break;
                }
            }
            users.addAll(augment);
        }
        return users;
    }
}
