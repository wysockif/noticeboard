package pl.wysockif.noticeboard.dto.notice.requests;

import lombok.Data;
import pl.wysockif.noticeboard.constraints.user.image.ImageWithSupportedExtension;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class PutNoticeRequest {

    @NotNull
    @Size(min = 8, max = 60)
    private String title;

    @NotNull
    @Size(min = 60, max = 2000)
    private String description;

    @NotNull
    @Size(min = 3, max = 60)
    private String location;

    @NotNull
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$",
            message = "{noticeboard.constraints.Pattern.Price.message}")
    @Size(min = 1, max = 10)
    private String price;

    @ImageWithSupportedExtension
    private String primaryImage;

    @ImageWithSupportedExtension
    private String secondaryImage;

    @ImageWithSupportedExtension
    private String tertiaryImage;

}
