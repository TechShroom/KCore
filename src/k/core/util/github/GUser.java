package k.core.util.github;

import java.util.ArrayList;

import k.core.util.github.gitjson.GithubJsonParser;

public class GUser {
    protected ArrayList<GRepo> created_repos = new ArrayList<GRepo>(),
            contributed_repos = new ArrayList<GRepo>(),
            starred_repos = new ArrayList<GRepo>();
    protected ArrayList<GOrg> owned_orgs = new ArrayList<GOrg>(),
            member_orgs = new ArrayList<GOrg>();

    protected String name = "";

    protected GUser(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    static GUser from(GData data) {
        String name = GithubJsonParser.begin(data.getData()).data("login")
                .toString();
        return new GUser(name.replace("\"", ""));
    }
}
