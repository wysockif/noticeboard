package pl.wysockif.noticeboard.errors.notice;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@ResponseStatus(FORBIDDEN)
public class DeletingNoticeForbiddenException extends RuntimeException {
    public DeletingNoticeForbiddenException(String message) {
        super(message);
    }
}
