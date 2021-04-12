package pl.wysockif.noticeboard.services.user;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.user.AppUser;
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
}
