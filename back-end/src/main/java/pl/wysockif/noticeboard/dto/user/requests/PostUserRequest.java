package pl.wysockif.noticeboard.dto.user.requests;

import lombok.Data;
import pl.wysockif.noticeboard.constraints.user.email.UniqueEmail;
import pl.wysockif.noticeboard.constraints.user.username.UniqueUsername;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class PostUserRequest {

    @NotNull
    @Size(min = 6, max = 60)
    @UniqueUsername
    private String username;

    @NotNull
    @Email
    @UniqueEmail
    @Size(min = 6, max = 60)
    private String email;

    @NotNull
    @Size(min = 3, max = 60)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 60)
    private String lastName;

    @NotNull
    @Size(min = 8, max = 60)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "{noticeboard.constraints.Pattern.Password.message}")
    private String password;

    @Override
    public String toString() {
        return "{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
