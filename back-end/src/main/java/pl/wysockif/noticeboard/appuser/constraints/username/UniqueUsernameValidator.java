package pl.wysockif.noticeboard.appuser.constraints.username;

import org.springframework.beans.factory.annotation.Autowired;
import pl.wysockif.noticeboard.appuser.constraints.username.UniqueUsername;
import pl.wysockif.noticeboard.appuser.entity.AppUser;
import pl.wysockif.noticeboard.appuser.repository.AppUserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        AppUser existingUser = userRepository.findByUsername(username);
        return existingUser == null;
    }
}
