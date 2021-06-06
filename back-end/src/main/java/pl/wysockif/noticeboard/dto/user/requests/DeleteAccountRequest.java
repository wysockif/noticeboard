package pl.wysockif.noticeboard.dto.user.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteAccountRequest {

    @NotNull
    private String password;

}
