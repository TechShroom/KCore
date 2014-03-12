package k.core.util.github;

import java.util.Collection;

public interface UserLike {
    public String apiURL();
    
    public String avatarURL();
    
    public Collection<GRepo> createdRepos();
    
    public String login();
    
    public String name();
}
