package pl.wysockif.noticeboard.controllers.notice;

import lombok.Data;

@Data
public class GetNoticesRequestParams {
    private String username;
    private String minPrice;
    private String maxPrice;
    private String location;
    private String searched;
}
