package k.core.util.github;

import java.util.*;

import k.core.util.github.gitjson.GitHubJsonParser;

import com.google.gson.*;

public class GOrg implements ShortStringProvider, UserLike {
    public static class GTeam implements ShortStringProvider, UserLike {
        private HashSet<GUser> members = new HashSet<GUser>();
        private GOrg parorg = null;

        private GTeam(GOrg parent) {
            parorg = parent;
        }

        @Override
        public String toShortString() {
            return "NYI";
        }

        public boolean addMember(GUser member) {
            return members.add(member);
        }

        public boolean hasMember(GUser member) {
            return members.contains(member);
        }

        public boolean removeMember(GUser member) {
            return members.remove(member);
        }

        public GOrg org() {
            return parorg;
        }

        public static HashMap<String, GTeam> formURL(String teamsUrl) {
            System.err.println("loading teams from " + teamsUrl);
            GData sum = GNet.getData(GNet.extractEndOfUL(teamsUrl),
                    GNet.NO_HEADERS_SPECIFIED, Auth.TRY);
            System.err.println(sum.getData());
            JsonArray orgs = GitHubJsonParser.parser.parse(sum.getData())
                    .getAsJsonArray();
            HashMap<String, GTeam> map = new HashMap<String, GTeam>(orgs.size());
            for (JsonElement je : orgs) {
                // we need to pull the right data
                int id = je.getAsJsonObject().get("id").getAsInt();
                String teamUrl = "/teams/" + id;
                GData team = GNet.getData(teamUrl, GNet.NO_HEADERS_SPECIFIED,
                        Auth.TRY);
                GitHubJsonParser in = GitHubJsonParser.begin(team.getData());
                List<GUser> users = GUser.listFromUrl(GNet.getData(teamUrl,
                        GNet.NO_HEADERS_SPECIFIED, Auth.TRY));
            }
            return map;
        }

        @Override
        public String apiURL() {
            return null;
        }

        @Override
        public String avatarURL() {
            return null;
        }

        @Override
        public Collection<GRepo> createdRepos() {
            return null;
        }

        @Override
        public String login() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }

    }

    public static List<GOrg> listFromUrl(String orgsUrl, GUser owner) {
        GData sum = GNet.getData(GNet.extractEndOfUL(orgsUrl),
                GNet.NO_HEADERS_SPECIFIED, Auth.TRY);
        System.err.println(sum.getData());
        JsonArray orgs = GitHubJsonParser.parser.parse(sum.getData())
                .getAsJsonArray();
        List<GOrg> list = new ArrayList<GOrg>(orgs.size());
        for (JsonElement je : orgs) {
            JsonObject o = je.getAsJsonObject();
            GOrg org = fromUrl(o.get("url").toString(), owner);
            list.add(org);
        }
        System.err.println(list);
        return list;
    }

    public static GOrg fromUrl(String orgUrl, GUser owner) {
        GitHubJsonParser in = GitHubJsonParser.begin(GNet.getData(orgUrl,
                GNet.NO_HEADERS_SPECIFIED, Auth.TRY).getData());
        String name = in.data("login").getAsString();
        int id = in.data("id").getAsInt();
        String apiUrl = in.data("url").getAsString();
        String picUrl = in.data("avatar_url").getAsString();
        HashMap<String, GTeam> teams = GTeam.formURL(apiUrl + "/teams");
        GOrg org = new GOrg(name, apiUrl, id, picUrl, owner, teams);
        return org;
    }

    private GUser owner = null;
    private HashMap<String, GTeam> teams = new HashMap<String, GTeam>();
    private Set<GRepo> created_repos = new HashSet<GRepo>();
    private String name, apiurl, picurl;

    private int id;

    private GOrg(String login, String url, int id, String picUrl, GUser owner,
            HashMap<String, GTeam> tset) {
        name = login;
        apiurl = url;
        this.id = id;
        picurl = picUrl;
        this.owner = owner;
        teams = tset;
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

    public void setOwner(GUser owner) {
        this.owner = owner;
        teams.get("owners").addMember(owner);
    }

    @Override
    public String toShortString() {
        return name + " (#" + id + "@" + apiurl + ")";
    }

    @Override
    public String toString() {
        return toShortString() + " is owned by " + owner.name()
                + " and has members "
                + ShortStringTransformer.asShortStringMap(teams);
    }
}
