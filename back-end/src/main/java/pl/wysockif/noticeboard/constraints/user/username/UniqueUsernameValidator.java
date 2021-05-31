package pl.wysockif.noticeboard.constraints.user.username;

import org.springframework.beans.factory.annotation.Autowired;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;

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
