package pl.wysockif.noticeboard.controllers.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.wysockif.noticeboard.dto.user.requests.PatchUserRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.dto.user.snapshots.AppUserSnapshot;
import pl.wysockif.noticeboard.services.user.AppUserService;

import javax.validation.Valid;
import java.util.logging.Logger;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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
        Long savedUserId = appUserService.saveUser(postUserRequest);
        LOGGER.info("Request postAppUser finished (userId: " + savedUserId + ")");
        return savedUserId;
    }

    @GetMapping("/users/{username}")
    @ResponseStatus(OK)
    public AppUserSnapshot getUserByUsername(@PathVariable String username) {
        LOGGER.info("Request getUserByUsername started (username: " + username + ")");
        AppUserSnapshot snapshot = appUserService.getUserByUsername(username);
        LOGGER.info("Request getUserByUsername finished (userId: " + snapshot.getId() + ")");
        return snapshot;
    }

    @GetMapping("/users/notice/{noticeId:[0-9]+}")
    @ResponseStatus(OK)
    public AppUserSnapshot getUserByUsername(@PathVariable Long noticeId) {
        LOGGER.info("Request getUserByNoticeId started (noticeId: " + noticeId + ")");
        AppUserSnapshot snapshot = appUserService.getUserByNoticeId(noticeId);
        LOGGER.info("Request getUserByNoticeId finished (userId: " + snapshot.getId() + ")");
        return snapshot;
    }

    @PatchMapping("/users/{id:[0-9]+}")
    @PreAuthorize("#id == principal.id")
    @ResponseStatus(OK)
    public AppUserSnapshot updateUser(@PathVariable Long id, @Valid @RequestBody PatchUserRequest patchUserRequest) {
        LOGGER.info("Request updateUser started (userId: " + id + ")");
        AppUserSnapshot snapshot = appUserService.update(id, patchUserRequest);
        LOGGER.info("Request updateUser finished (userId: " + id + ")");
        return snapshot;
    }

    @PatchMapping("/users/{email}/activation")
    public AppUserSnapshot activateAccount(@PathVariable String email) {
        LOGGER.info("Request activateAccount started (userEmail: " + email + ")");
        AppUserSnapshot snapshot = appUserService.activateAccount(email);
        LOGGER.info("Request activateAccount started (userEmail: " + email + ")");
        return snapshot;
    }
}
