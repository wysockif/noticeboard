package pl.wysockif.noticeboard.dto.notice.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PostNoticeRequest {

    @NotNull
    @Size(min = 8, max = 30)
    private String title;

    @NotNull
    @Size(min = 60, max = 2000)
    private String description;

    @NotNull
    private String primaryImage;

    @NotNull
    private String secondaryImage;

    @NotNull
    private String tertiaryImage;

    @NotNull
    private String location;

    @NotNull
    private List<String> keywords;

}
