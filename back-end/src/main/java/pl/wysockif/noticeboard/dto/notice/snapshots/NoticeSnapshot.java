package pl.wysockif.noticeboard.dto.notice.snapshots;

import lombok.Data;

import java.util.Date;

@Data
public class NoticeSnapshot {

    private Long id;

    private String title;

    private String location;

    private String price;

    private String primaryImage;

    private Date createdAt;

}
