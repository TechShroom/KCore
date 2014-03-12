package k.core.util.github;

import java.util.*;

import k.core.util.github.gitjson.GitHubJsonParser;

public class GUser implements ShortStringProvider, UserLike {
    static HashMap<String, GUser> users = new HashMap<String, GUser>();

    static GUser from(GData data) {
        return from(data.getData());
    }

    static GUser from(String data) {
        GitHubJsonParser base = GitHubJsonParser.begin(data);
        String name = base.data("login").getAsString().replace("\"", "");
        System.err.println(base.toString());
        GUser user = users.get(name);
        if (user == null) {
            user = new GUser(name);
            List<GOrg> in = GOrg.fromURL(base.data("organizations_url")
                    .getAsString(), user);
            user.owned_orgs.addAll(in);
        }
        return user;
    }

    protected Set<GRepo> created_repos = new HashSet<GRepo>(),
            contributed_repos = new HashSet<GRepo>(),
            starred_repos = new HashSet<GRepo>();

    protected String login;

    protected String name = "";

    protected Set<GOrg> owned_orgs = new HashSet<GOrg>(),
            member_orgs = new HashSet<GOrg>();

    protected GUser(String name) {
        this.name = name;
        users.put(name, this);
    }

    @Override
    public String apiURL() {
        return null;
    }

    @Override
    public String avatarURL() {
        return null;
    }

    public Set<GRepo> contributedRepos() {
        return contributed_repos;
    }

    @Override
    public Collection<GRepo> createdRepos() {
        return created_repos;
    }

    @Override
    public String login() {
        return login;
    }

    public Set<GOrg> memeberOrgs() {
        return member_orgs;
    }

    @Override
    public String name() {
        return name;
    }

    public Set<GOrg> ownerOrgs() {
        return owned_orgs;
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
        org.setOwner(this);
    }

    public Set<GRepo> starredRepos() {
        return starred_repos;
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
