package pl.wysockif.noticeboard.dto.user.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class DeleteAccountRequest {

    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "{noticeboard.constraints.Pattern.Password.message}")
    private String password;

}
