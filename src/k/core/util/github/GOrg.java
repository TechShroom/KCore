package k.core.util.github;

import java.util.*;

import k.core.util.github.gitjson.GitHubJsonParser;

import com.google.gson.*;

public class GOrg implements ShortStringProvider, UserLike {
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
            GOrg org = new GOrg(name, apiUrl, id, picUrl, owner);
            org.members.add(owner);
            list.add(org);
        }
        System.err.println(list);
        return list;
    }
    private GUser owner = null;
    private HashSet<GUser> members = new HashSet<GUser>();
    private Set<GRepo> created_repos = new HashSet<GRepo>();
    private String name, apiurl, picurl;

    private int id;

    private GOrg(String login, String url, int id, String picUrl, GUser owner) {
        name = login;
        apiurl = url;
        this.id = id;
        picurl = picUrl;
        this.owner = owner;
    }

    public boolean addMember(GUser member) {
        return members.add(member);
    }

    @Override
    public String apiURL() {
        return apiurl;
    }

    @Override
    public String avatarURL() {
        return picurl;
    }

    @Override
    public Collection<GRepo> createdRepos() {
        return created_repos;
    }

    public boolean hasMember(GUser member) {
        return members.contains(member);
    }

    public int id() {
        return id;
    }

    @Override
    public String login() {
        return name();
    }

    @Override
    public String name() {
        return name;
    }

    public GUser owner() {
        return owner;
    }

    public boolean removeMember(GUser member) {
        return members.remove(member);
    }

    public void setOwner(GUser owner) {
        this.owner = owner;
        addMember(owner);
    }

    @Override
    public String toShortString() {
        return name + " (#" + id + "@" + apiurl + ")";
    }

    @Override
    public String toString() {
        return toShortString() + " is owned by " + owner.name()
                + " and has members "
                + ShortStringTransformer.asShortStringCollection(members);
    }
}
