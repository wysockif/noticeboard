package pl.wysockif.noticeboard.appuser.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.appuser.entity.AppUser;
import pl.wysockif.noticeboard.appuser.repository.AppUserRepository;

@Service
public class AppUserService {
    private final AppUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AppUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public AppUser save(AppUser user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
