package pl.wysockif.noticeboard.dto.user.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class PatchUserRequest {
    @NotNull
    @Size(min = 3, max = 64)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 64)
    private String lastName;

    private String profileImage;

    public PatchUserRequest(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}