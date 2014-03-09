package k.core.util.github;

import static k.core.util.core.Helper.Base64.toB64;

public final class GAuth {
    private String tken, loc;

    GAuth(String token, String location) {
        tken = token.replace("\"", "");
        loc = location;
    }

    @Override
    public String toString() {
        return tken + "@" + loc;
    }

    public static String basic(String user, String pass) {
        return "Basic " + toB64(user + ":" + pass);
    }

}
