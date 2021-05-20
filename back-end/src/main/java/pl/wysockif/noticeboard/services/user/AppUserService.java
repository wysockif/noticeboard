package pl.wysockif.noticeboard.services.user;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.dto.user.requests.PatchUserRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.dto.user.snapshots.AppUserSnapshot;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.user.AlreadyActivatedUserException;
import pl.wysockif.noticeboard.errors.user.UserNotFoundException;
import pl.wysockif.noticeboard.mappers.user.AppUserMapper;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.file.StaticFileService;
import pl.wysockif.noticeboard.services.token.VerificationTokenService;

import java.util.Optional;
import java.util.logging.Logger;

import static java.lang.String.valueOf;

@Service
public class AppUserService {

    @Value("${lock-user-account-on-start}")
    private Boolean activeProfile;

    private final Logger LOGGER = Logger.getLogger(AppUserService.class.getName());

    private final AppUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final StaticFileService staticFileService;

    private final VerificationTokenService tokenService;


    public AppUserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder,
                          StaticFileService staticFileService, VerificationTokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.staticFileService = staticFileService;
        this.tokenService = tokenService;
    }

    public Long saveUser(PostUserRequest postUserRequest) {
        LOGGER.info("Saving user: " + postUserRequest);
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(postUserRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setLockedAccount(activeProfile);
        AppUser savedUser = userRepository.save(user);
        Long savedUserId = savedUser.getId();
        tokenService.sendNewToken(savedUser);
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

    public AppUserSnapshot getUserByNoticeId(Long noticeId) {
        LOGGER.info("Getting user by noticeId: " + noticeId);
        AppUser appUser = userRepository.findByNoticesId(noticeId);
        if (appUser == null) {
            LOGGER.info("Not found user by noticeId: " + noticeId);
            throw new UserNotFoundException("Nie znalaziono użytkownika o po id ogłoszenia: " + noticeId);
        }
        LOGGER.info("Got user: " + appUser.getId());
        return AppUserMapper.INSTANCE.appUserToSnapshot(appUser);
    }

    public void unlockUserAccount(Long userId) {
        LOGGER.info("Unlocking user (userId: " + userId + ")");
        Optional<AppUser> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            LOGGER.info("Cannot unlock non-existing user (userId: " + userId + ")");
            throw new UserNotFoundException("Nie znaleziono użytkownika");
        }
        AppUser appUser = userOptional.get();
        appUser.setLockedAccount(false);
        userRepository.save(appUser);
        LOGGER.info("Unlocked user (userId: " + userId + ")");
    }

    public AppUserSnapshot activateAccount(String email) {
        LOGGER.info("Activating user account (userEmail: " + email + ")");
        Optional<AppUser> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            LOGGER.info("Cannot activate non-existing user account (userEmail: " + email + ")");
            throw new UserNotFoundException("Nie znaleziono użytkownika o podanym adresie e-mail");
        } else if(!userOptional.get().isLockedAccount()){
            LOGGER.info("Cannot activate activated user account (userEmail: " + email + ")");
            throw new AlreadyActivatedUserException("Użytkownik o podanym adresie e-mail został aktywowany już wcześniej");
        }
        tokenService.sendNewToken(userOptional.get());
        LOGGER.info("Activated user account (userEmail: " + email + ")");
        return AppUserMapper.INSTANCE.appUserToSnapshot(userOptional.get());
    }
}
