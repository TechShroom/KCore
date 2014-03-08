package k.core.util.github;

import java.util.ArrayList;

public class GUser {
    private ArrayList<GRepo> created_repos = new ArrayList<GRepo>(),
            contributed_repos = new ArrayList<GRepo>(),
            starred_repos = new ArrayList<GRepo>();
    private ArrayList<GOrg> owned_orgs = new ArrayList<GOrg>(),
            member_orgs = new ArrayList<GOrg>();
}
