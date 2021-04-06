package pl.wysockif.noticeboard.appuser.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.wysockif.noticeboard.appuser.dto.requests.PostUserRequest;
import pl.wysockif.noticeboard.appuser.service.AppUserService;

import java.util.logging.Logger;

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
    public Long postAppUser(@RequestBody PostUserRequest postUserRequest) {
        LOGGER.info("Request postAppUser started (user: " + postUserRequest + ")");
        Long savedUserId = appUserService.save(postUserRequest);
        LOGGER.info("Request postAppUser finished (userId: " + savedUserId + ")");
        return savedUserId;
    }
}
