package pl.wysockif.noticeboard.dto.notice.snapshots;

import lombok.Data;

import java.util.Date;

@Data
public class NoticeWithDetailsSnapshot {

    private Long id;

    private Date createdAt;

    private String title;

    private String description;

    private String location;

    private String price;

    private String primaryImage;

    private String secondaryImage;

    private String tertiaryImage;
}
