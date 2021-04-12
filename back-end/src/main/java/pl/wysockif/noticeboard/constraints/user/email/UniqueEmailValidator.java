package pl.wysockif.noticeboard.constraints.user.email;

import org.springframework.beans.factory.annotation.Autowired;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        AppUser existingUser = userRepository.findByEmail(email);
        return existingUser == null;
    }
}
