package pl.wysockif.noticeboard.errors.token;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class IncorrectTokenException extends RuntimeException {

    public IncorrectTokenException(String message) {
        super(message);
    }

}
