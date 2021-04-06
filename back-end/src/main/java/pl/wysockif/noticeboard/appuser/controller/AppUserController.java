package pl.wysockif.noticeboard.appuser.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.wysockif.noticeboard.appuser.entity.AppUser;
import pl.wysockif.noticeboard.appuser.service.AppUserService;
import pl.wysockif.noticeboard.shared.Response;

@RestController
public class AppUserController {
    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("api/1.0/users")
    public Response postAppUser(@RequestBody AppUser appUser) {
        appUserService.save(appUser);
        return new Response("User saved");
    }


}
