package k.core.util.github;

import java.util.*;

import k.core.util.github.gitjson.GitHubJsonParser;

public class GUser implements ShortStringProvider {
    static HashMap<String, GUser> users = new HashMap<String, GUser>();

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

    protected Set<GRepo> created_repos = new HashSet<GRepo>(),
            contributed_repos = new HashSet<GRepo>(),
            starred_repos = new HashSet<GRepo>();

    protected Set<GOrg> owned_orgs = new HashSet<GOrg>(),
            member_orgs = new HashSet<GOrg>();

    protected String name = "";

    protected GUser(String name) {
        this.name = name;
        users.put(name, this);
    }

    public Set<GRepo> getContributedRepos() {
        return contributed_repos;
    }

    public Set<GRepo> getCreatedRepos() {
        return created_repos;
    }

    public Set<GOrg> getMemberOrgs() {
        return member_orgs;
    }

    public Set<GOrg> getOwnedOrgs() {
        return owned_orgs;
    }

    public Set<GRepo> getStarredRepos() {
        return starred_repos;
    }

    public String name() {
        return name;
    }

    public void setMemberOf(GOrg org) {
        if (!org.hasMember(this)) {
            org.addMember(this);
            member_orgs.add(org);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerOf(GOrg org) {

    }

    @Override
    public String toShortString() {
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
}
