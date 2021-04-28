package pl.wysockif.noticeboard.entities.notice;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Data
public class Notice {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Temporal(TIMESTAMP)
    private Date createdAt;

    private String title;

    @Column(length = 2000)
    private String description;

    private String primaryImage;

    private String secondaryImage;

    private String tertiaryImage;

    private String location;

    private String price;

    private String keywords;

}
