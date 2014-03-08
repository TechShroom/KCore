package k.core.util.github.gitjson;

import java.util.HashMap;
import java.util.regex.Pattern;

import k.core.util.core.Helper.BetterArrays;

import com.google.gson.*;

public final class GithubJsonParser {
    private static final JsonParser parser = new JsonParser();

    /**
     * Path cache, saves lookup time on really long paths
     */
    private GithubJsonParser parent = null;
    private transient HashMap<String, JsonElement> lookedUpPaths = new HashMap<String, JsonElement>();
    private JsonObject relatedObj = null;

    public GithubJsonParser(String data) {
        this(parser.parse(data).getAsJsonObject(), null);
    }

    private GithubJsonParser(JsonObject obj, GithubJsonParser par) {
        relatedObj = obj;
        parent = par;
        lookedUpPaths.put("/", relatedObj);
    }

    public static GithubJsonParser begin(String data) {
        return new GithubJsonParser(data);
    }

    public JsonElement data(String path) {
        // normalize for mappings
        path = path.replaceFirst("^/", "");
        if (!path.endsWith("/")) {
            path += "/";
        }
        // split up trail, reverse it
        String[] trailReverse = BetterArrays.reverse(path.split("/"));
        String pathSoFar = path;
        // shortcut values
        if (lookedUpPaths.containsKey(pathSoFar)) {
            return lookedUpPaths.get(pathSoFar);
        }
        JsonElement currObject = relatedObj;
        // find already looked up parts
        for (String s : trailReverse) {
            if (lookedUpPaths.containsKey(pathSoFar)) {
                currObject = lookedUpPaths.get(pathSoFar);
                break;
            }
            // remove path part we just tested
            pathSoFar = pathSoFar.replaceFirst(Pattern.quote(s) + "/$", "");
        }
        // must do trail here, so that we discard the part of the trail that we
        // don't use
        String[] trail = path.replace(pathSoFar, "").split("/");
        // trace trail to find object, storing values as we find them in the
        // cache
        for (int i = 0; i < trail.length; i++) {
            String s = trail[i];
            pathSoFar += s + "/";
            if (currObject.isJsonObject()) {
                currObject = currObject.getAsJsonObject().get(s);
            } else {
                throw new IllegalArgumentException(path + " does not exist in "
                        + relatedObj);
            }
        }
        return currObject;
    }

    public GithubJsonParser subparser(String path) {
        JsonElement elem = data(path);
        if (elem.isJsonObject()) {
            return new GithubJsonParser(elem.getAsJsonObject(), this);
        } else {
            throw new UnsupportedOperationException(
                    "Cannot create parser for nothing");
        }
    }

    @Override
    public String toString() {
        return relatedObj.toString();
    }

    /**
     * Calls {@link #end(boolean)} with a value of false, telling it to call end
     * on parent parsers.
     */
    public void end() {
        end(false);
    }

    /**
     * Ends this parsers usage, cleaning up any resources. Optionally closes
     * parent parsers as well.
     * 
     * @param keepParentAlive
     *            - if we should keep the parent parsers alive.
     */
    public void end(boolean keepParentAlive) {
        relatedObj = null;
        if (parent != null && !keepParentAlive) {
            parent.end(false);
        }
    }

}
