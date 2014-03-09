package k.core.util.github;

import static k.core.util.core.Helper.Base64.toB64;

public final class GAuth {
    private String authvalue, loc;

    GAuth(String authval, String location) {
        authvalue = authval.replace("\"", "");
        loc = location;
    }

    @Override
    public String toString() {
        return authvalue + "@" + loc;
    }

    public static String basic(String user, String pass) {
        return "Basic " + toB64(user + ":" + pass);
    }

    public static String token(String token) {
        return "token " + token;
    }

    public String getAuthValue() {
        return authvalue;
    }

}
