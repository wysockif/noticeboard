package pl.wysockif.noticeboard.services.user;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.dto.user.requests.PatchUserRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.dto.user.snapshots.AppUserSnapshot;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.user.UserNotFoundException;
import pl.wysockif.noticeboard.mappers.user.AppUserMapper;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.file.StaticFileService;

import java.util.logging.Logger;

import static java.lang.String.valueOf;

@Service
public class AppUserService {

    private final AppUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final StaticFileService staticFileService;

    private final Logger LOGGER = Logger.getLogger(AppUserService.class.getName());

    public AppUserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder, StaticFileService staticFileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.staticFileService = staticFileService;
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

    public AppUserSnapshot update(Long id, PatchUserRequest patchUserRequest) {
        LOGGER.info("Updating user: " + id);
        AppUser appUser = userRepository.getOne(id);
        appUser.setFirstName(patchUserRequest.getFirstName());
        appUser.setLastName(patchUserRequest.getLastName());

        if (patchUserRequest.getProfileImage() != null) {
            String profileImageName = staticFileService
                    .saveProfileImage(valueOf(appUser.getId()), appUser.getUsername(), patchUserRequest.getProfileImage());
            staticFileService.deleteOldProfileImage(valueOf(appUser.getId()), appUser.getImage());
            appUser.setImage(profileImageName);
        }
        userRepository.save(appUser);
        LOGGER.info("Updated user: " + id);
        return AppUserMapper.INSTANCE.appUserToSnapshot(appUser);
    }
}
