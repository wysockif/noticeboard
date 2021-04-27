package pl.wysockif.noticeboard.errors;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@ResponseStatus(SERVICE_UNAVAILABLE)
public class FileIOException extends RuntimeException {
    public FileIOException() {
        super("Cannot create file");
    }
}
