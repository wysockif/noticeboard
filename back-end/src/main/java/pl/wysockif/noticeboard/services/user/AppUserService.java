package pl.wysockif.noticeboard.services.user;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.dto.user.snapshots.AppUserSnapshot;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.user.UserNotFoundException;
import pl.wysockif.noticeboard.mappers.user.AppUserMapper;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;

import java.util.logging.Logger;

@Service
public class AppUserService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger LOGGER = Logger.getLogger(AppUserService.class.getName());

    public AppUserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long save(PostUserRequest postUserRequest) {
        LOGGER.info("Saving user: " + postUserRequest);
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(postUserRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        AppUser savedUser = userRepository.save(user);
        Long savedUserId = savedUser.getId();
        LOGGER.info("Saved user (userId: " + savedUserId + ")");
        return savedUser.getId();
    }

    public AppUserSnapshot getUserByUsername(String username) {
        LOGGER.info("Getting user: " + username);
        AppUser appUser = userRepository.findByUsername(username);
        if (appUser == null) {
            LOGGER.info("Not found user: " + username);
            throw new UserNotFoundException("Nie znalaziono użytkownika o nazwie użytkownika: " + username);
        }
        AppUserSnapshot snapshot = AppUserMapper.INSTANCE.appUserToSnapshot(appUser);
        LOGGER.info("Got user: " + snapshot.getId());
        return snapshot;
    }
}
