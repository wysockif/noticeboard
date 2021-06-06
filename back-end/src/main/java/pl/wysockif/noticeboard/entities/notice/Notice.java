package pl.wysockif.noticeboard.entities.notice;

import lombok.Data;
import pl.wysockif.noticeboard.entities.user.AppUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Data
public class Notice implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Temporal(TIMESTAMP)
    private Date createdAt;

    @Column(length = 60)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 60)
    private String location;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private AppUser creator;

    private String primaryImage;

    private String secondaryImage;

    private String tertiaryImage;
}
