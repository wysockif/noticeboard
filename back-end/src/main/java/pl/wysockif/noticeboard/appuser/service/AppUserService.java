package pl.wysockif.noticeboard.appuser.service;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.appuser.dto.requests.PostUserRequest;
import pl.wysockif.noticeboard.appuser.entity.AppUser;
import pl.wysockif.noticeboard.appuser.mapper.AppUserMapper;
import pl.wysockif.noticeboard.appuser.repository.AppUserRepository;

import java.util.logging.Logger;

@Service
public class AppUserService {
    private final AppUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Logger LOGGER = Logger.getLogger(AppUserService.class.getName());

    public AppUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public Long save(PostUserRequest postUserRequest) {
        LOGGER.info("Saving user: " + postUserRequest);
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(postUserRequest);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        AppUser savedUser = userRepository.save(user);
        Long savedUserId = savedUser.getId();
        LOGGER.info("Saved user (userId: " + savedUserId + ")");
        return savedUser.getId();
    }
}
