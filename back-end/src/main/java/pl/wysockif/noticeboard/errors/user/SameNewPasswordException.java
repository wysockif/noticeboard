package pl.wysockif.noticeboard.errors.user;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class SameNewPasswordException extends RuntimeException {

    public SameNewPasswordException(String message) {
        super(message);
    }

}
