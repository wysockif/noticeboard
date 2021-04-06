package pl.wysockif.noticeboard.appuser.dto.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.wysockif.noticeboard.appuser.entity.AppUser;

@Data
@NoArgsConstructor
public class PostUserRequest {
    private String username;
    private String email;
    private String displayName;
    private String password;

    public PostUserRequest(AppUser appUser){
        this.username = appUser.getUsername();
        this.email = appUser.getEmail();
        this.displayName = appUser.getDisplayName();
        this.password = appUser.getPassword();
    }

    public AppUser toAppUser() {
        AppUser appUser = new AppUser();
        appUser.setUsername(this.username);
        appUser.setEmail(this.email);
        appUser.setDisplayName(this.displayName);
        appUser.setPassword(this.password);
        return appUser;
    }

    @Override
    public String toString() {
        return "PostUserRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
