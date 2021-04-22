package pl.wysockif.noticeboard.controllers.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.dto.user.snapshots.AppUserSnapshot;
import pl.wysockif.noticeboard.services.user.AppUserService;

import javax.validation.Valid;
import java.util.logging.Logger;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/1.0")
public class AppUserController {
    private final AppUserService appUserService;
    private static final Logger LOGGER = Logger.getLogger(AppUserController.class.getName());

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/users")
    @ResponseStatus(CREATED)
    public Long addUser(@Valid @RequestBody PostUserRequest postUserRequest) {
        LOGGER.info("Request postAppUser started (user: " + postUserRequest + ")");
        Long savedUserId = appUserService.save(postUserRequest);
        LOGGER.info("Request postAppUser finished (userId: " + savedUserId + ")");
        return savedUserId;
    }

    @GetMapping("/users/{username}")
    public AppUserSnapshot getUserByUsername(@PathVariable String username) {
        LOGGER.info("Request getUserByUsername started (username: " + username + ")");
        AppUserSnapshot snapshot = appUserService.getUserByUsername(username);
        LOGGER.info("Request getUserByUsername finished (userId: " + snapshot.getId() + ")");
        return snapshot;
    }
}
