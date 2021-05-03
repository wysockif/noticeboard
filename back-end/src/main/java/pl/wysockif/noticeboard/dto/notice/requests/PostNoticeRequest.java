package pl.wysockif.noticeboard.dto.notice.requests;

import lombok.Data;
import pl.wysockif.noticeboard.constraints.user.image.ImageWithSupportedExtension;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PostNoticeRequest {

    @NotNull
    @Size(min = 8, max = 60)
    private String title;

    @NotNull
    @Size(min = 60, max = 2000)
    private String description;

    @NotNull
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$",
            message = "{noticeboard.constraints.Pattern.Price.message}")
    @Size(min = 1, max = 10)
    private String price;

    @NotNull(message = "{noticeboard.constraints.Image.NotNull.message}")
    @ImageWithSupportedExtension
    private String primaryImage;

    @NotNull(message = "{noticeboard.constraints.Image.NotNull.message}")
    @ImageWithSupportedExtension
    private String secondaryImage;

    @NotNull(message = "{noticeboard.constraints.Image.NotNull.message}")
    @ImageWithSupportedExtension
    private String tertiaryImage;

    @NotNull
    @Size(min = 3, max = 60)
    private String location;

    @NotNull
    @Size(min = 3, max = 12, message = "{noticeboard.constraints.Size.KeywordsList.message}")
    private List<@NotNull @Size(min = 3, max = 20, message = "{noticeboard.constraints.Size.Keyword.message}") String> keywords;

}
