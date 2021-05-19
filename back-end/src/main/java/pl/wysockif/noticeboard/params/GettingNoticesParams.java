package pl.wysockif.noticeboard.params;

import lombok.Data;

@Data
public class GettingNoticesParams {
    private String username;
    private String minPrice;
    private String maxPrice;
    private String location;
    private String searched;
}
