package pl.wysockif.noticeboard.configuration;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.appuser.entity.AppUser;
import pl.wysockif.noticeboard.appuser.repository.AppUserRepository;

@Service
public class AuthUserService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public AuthUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
