package pl.wysockif.noticeboard.appuser.dto.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class PostUserRequest {
    @NotNull
    @Size(min = 6, max = 64)
    private String username;

    @NotNull
    @Email
    @Size(min = 5, max = 64)
    private String email;

    @NotNull
    @Size(min = 3, max = 64)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 64)
    private String lastName;

    @NotNull
    @Size(min = 8, max = 64)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")
    private String password;

    @Override
    public String toString() {
        return "{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
