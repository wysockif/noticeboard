package pl.wysockif.noticeboard.entities.token;

import lombok.Data;
import pl.wysockif.noticeboard.entities.user.AppUser;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Data
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Temporal(TIMESTAMP)
    private Date generatedAt;

    private String value;

    @ManyToOne
    private AppUser appUser;

}
