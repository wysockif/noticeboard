package pl.wysockif.noticeboard.errors.notice;


import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class NoticeNotFoundException extends RuntimeException {

    public NoticeNotFoundException(String message) {
        super(message);
    }

}
