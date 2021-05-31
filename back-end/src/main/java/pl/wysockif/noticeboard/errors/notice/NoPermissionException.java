package pl.wysockif.noticeboard.errors.notice;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@ResponseStatus(FORBIDDEN)
public class NoPermissionException extends RuntimeException {

    public NoPermissionException(String message) {
        super(message);
    }

}
