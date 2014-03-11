package k.core.util.github;

import java.util.*;

import k.core.util.github.gitjson.GitHubJsonParser;

public class GUser implements ShortStringProvider {
    static HashMap<String, GUser> users = new HashMap<String, GUser>();
    protected List<GRepo> created_repos = new ArrayList<GRepo>(),
            contributed_repos = new ArrayList<GRepo>(),
            starred_repos = new ArrayList<GRepo>();
    protected List<GOrg> owned_orgs = new ArrayList<GOrg>(),
            member_orgs = new ArrayList<GOrg>();

    protected String name = "";

    protected GUser(String name) {
        this.name = name;
        users.put(name, this);
    }

    public void setMemberOf(GOrg org) {
        if (!org.hasMember(this)) {
            org.addMember(this);
        }
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return String
                .format("User %s has created repos %s, contributed to %s, starred %S, started %s, and is a member of %s.",
                        name, ShortStringTransformer
                                .asShortStringCollection(created_repos),
                        ShortStringTransformer
                                .asShortStringCollection(contributed_repos),
                        ShortStringTransformer
                                .asShortStringCollection(starred_repos),
                        ShortStringTransformer
                                .asShortStringCollection(owned_orgs),
                        ShortStringTransformer
                                .asShortStringCollection(member_orgs));
    }

    static GUser from(GData data) {
        GitHubJsonParser base = GitHubJsonParser.begin(data.getData());
        String name = base.data("login").getAsString().replace("\"", "");
        System.err.println(base.toString());
        GUser user = users.get(name);
        if (user == null) {
            user = new GUser(name);
        }
        List<GOrg> in = GOrg.fromURL(base.data("organizations_url")
                .getAsString(), user);
        user.owned_orgs.addAll(in);
        return user;
    }

    @Override
    public String toShortString() {
        return name;
    }
}
