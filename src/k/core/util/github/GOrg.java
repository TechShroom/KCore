package k.core.util.github;

import java.util.*;

import k.core.util.github.gitjson.GitHubJsonParser;

import com.google.gson.*;

public class GOrg {
    private GUser owner = null;
    private HashSet<GUser> members = new HashSet<GUser>();
    private String name, apiurl, picurl;
    private int id;

    private GOrg(String login, String url, int id, String picUrl, GUser owner) {
        name = login;
        apiurl = url;
        this.id = id;
        picurl = picUrl;
        this.owner = owner;
    }

    public String name() {
        return name;
    }

    public String apiURL() {
        return apiurl;
    }

    public String picURL() {
        return picurl;
    }

    public int id() {
        return id;
    }

    @Override
    public String toString() {
        return name + " (#" + id + ") @ " + apiurl;
    }

    public static List<GOrg> fromURL(String orgUrl, GUser owner) {
        GData sum = GNet.getData(GNet.extractEndOfUL(orgUrl),
                GNet.NO_HEADERS_SPECIFIED, Auth.TRY);
        System.err.println(sum.getData());
        JsonArray orgs = GitHubJsonParser.parser.parse(sum.getData())
                .getAsJsonArray();
        List<GOrg> list = new ArrayList<GOrg>(orgs.size());
        for (JsonElement je : orgs) {
            JsonObject o = je.getAsJsonObject();
            GitHubJsonParser in = GitHubJsonParser.begin(o.toString());
            String name = in.data("login").getAsString();
            int id = in.data("id").getAsInt();
            String apiUrl = in.data("url").getAsString();
            String picUrl = in.data("avatar_url").getAsString();
            list.add(new GOrg(name, apiUrl, id, picUrl, owner));
        }
        System.err.println(list);
        return list;
    }
}
