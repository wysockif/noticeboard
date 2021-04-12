package pl.wysockif.noticeboard.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@RestController
public class ErrorHandler implements ErrorController {
    private final ErrorAttributes errorAttributes;

    public ErrorHandler(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public ApiError handleError(WebRequest webRequest) {
        String message = (String) errorAttributes
                .getErrorAttributes(webRequest, ErrorAttributeOptions.of(MESSAGE)).get("message");
        return new ApiError(message);
    }
}
