package pl.wysockif.noticeboard.dto.user.snapshots;

import lombok.Data;

@Data
public class AppUserSnapshot {
    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String image;
}
