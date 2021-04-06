package pl.wysockif.noticeboard.appuser.dto;

import lombok.Data;
import pl.wysockif.noticeboard.appuser.entity.AppUser;

@Data
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

}
