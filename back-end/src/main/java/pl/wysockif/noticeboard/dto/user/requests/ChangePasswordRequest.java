package pl.wysockif.noticeboard.dto.user.requests;

import lombok.Data;

@Data
public class ChangePasswordRequest {

    private String oldPassword;

    private String newPassword;

}
