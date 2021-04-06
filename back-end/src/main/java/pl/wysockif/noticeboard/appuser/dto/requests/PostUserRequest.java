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

    @Override
    public String toString() {
        return "PostUserRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
