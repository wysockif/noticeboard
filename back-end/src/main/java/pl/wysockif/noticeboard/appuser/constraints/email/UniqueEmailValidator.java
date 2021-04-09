package pl.wysockif.noticeboard.appuser.constraints.email;

import org.springframework.beans.factory.annotation.Autowired;
import pl.wysockif.noticeboard.appuser.entity.AppUser;
import pl.wysockif.noticeboard.appuser.repository.AppUserRepository;

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
