package pl.wysockif.noticeboard.appuser.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String displayName;
    private String password;
}
