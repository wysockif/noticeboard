package pl.wysockif.noticeboard.controllers.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wysockif.noticeboard.dto.user.snapshots.AppUserSnapshot;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.mappers.user.AppUserMapper;

@RestController
@RequestMapping("/api/1.0")
public class LoginController {

    @PostMapping("/login")
    public AppUserSnapshot login() {
        // https://stackoverflow.com/questions/31159075/how-to-find-out-the-currently-logged-in-user-in-spring-boot
        AppUser loggedInUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return AppUserMapper.INSTANCE.appUserToSnapshot(loggedInUser);
    }
}