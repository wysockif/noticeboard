package pl.wysockif.noticeboard.dto.user.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordRequest {

    @NotNull
    @Size(min = 8, max = 60)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "{noticeboard.constraints.Pattern.Password.message}")
    private String oldPassword;

    @NotNull
    @Size(min = 8, max = 60)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "{noticeboard.constraints.Pattern.Password.message}")
    private String newPassword;

}
