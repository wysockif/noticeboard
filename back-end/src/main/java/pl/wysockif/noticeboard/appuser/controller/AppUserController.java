package pl.wysockif.noticeboard.appuser.controller;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.wysockif.noticeboard.appuser.dto.requests.PostUserRequest;
import pl.wysockif.noticeboard.appuser.service.AppUserService;
import pl.wysockif.noticeboard.error.ApiError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class AppUserController {
    private final AppUserService appUserService;
    private static final Logger LOGGER = Logger.getLogger(AppUserController.class.getName());

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("api/1.0/users")
    @ResponseStatus(CREATED)
    public Long postAppUser(@Valid @RequestBody PostUserRequest postUserRequest) {
        LOGGER.info("Request postAppUser started (user: " + postUserRequest + ")");
        Long savedUserId = appUserService.save(postUserRequest);
        LOGGER.info("Request postAppUser finished (userId: " + savedUserId + ")");
        return savedUserId;
    }

    // https://stackoverflow.com/questions/9245487/how-to-handle-validation-errors-and-exceptions-in-a-restful-spring-mvc-controlle
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : exception.getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ApiError(BAD_REQUEST.value(), "Validation error", request.getServletPath(), validationErrors);
    }
}
