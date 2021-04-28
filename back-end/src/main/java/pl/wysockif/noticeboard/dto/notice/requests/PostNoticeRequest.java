package pl.wysockif.noticeboard.dto.notice.requests;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PostNoticeRequest {

    @NotNull
    @Size(min = 8, max = 50)
    private String title;

    @NotNull
    @Size(min = 60, max = 2000)
    private String description;

    @NotNull
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$")
    @Size(min = 1, max = 10)
    private String price;

    @NotNull
//    @ImageWithSupportedExtension
    private String primaryImage;

    @NotNull
//    @ImageWithSupportedExtension
    private String secondaryImage;

    @NotNull
//    @ImageWithSupportedExtension
    private String tertiaryImage;

    @NotNull
    @Size(min = 3, max = 64)
    private String location;

    @NotNull
    @Size(min = 3, max = 12)
    private List<@NotNull @Size(min = 3, max = 30) String> keywords;

}
