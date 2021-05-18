package pl.wysockif.noticeboard.services.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.dto.token.VerifyTokenResponse;
import pl.wysockif.noticeboard.dto.user.snapshots.AppUserSnapshot;
import pl.wysockif.noticeboard.entities.token.VerificationToken;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.token.IncorrectTokenException;
import pl.wysockif.noticeboard.repositories.token.VerificationTokenRepository;
import pl.wysockif.noticeboard.services.mail.MailService;
import pl.wysockif.noticeboard.services.user.AppUserService;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationTokenService {

    @Value("${verify-address-email-url}")
    private String linkToClick;

    private final MailService mailService;

    private final VerificationTokenRepository tokenRepository;

    public VerificationTokenService(VerificationTokenRepository tokenRepository, MailService mailService) {
        this.tokenRepository = tokenRepository;
        this.mailService = mailService;
    }

    @Autowired
    private AppUserService userService;

    public void generateToken(AppUser appUser) {
        VerificationToken token = new VerificationToken();
        String tokenValue = UUID.randomUUID().toString();
        token.setValue(tokenValue);
        token.setAppUser(appUser);
        tokenRepository.save(token);
        String url = linkToClick + "/" + tokenValue;
        try {
            mailService.sendMail(appUser.getEmail(), "Potwierdź to!", url, false);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void verifyToken(String tokenValue) {
        Optional<VerificationToken> token = tokenRepository.findByValue(tokenValue);
        if (token.isEmpty()) {
            throw new IncorrectTokenException("Użytkownik został już zweryfikowany albo token znajdujący się w linku jest nieprawidłowy.");
        }
        Long id = token.get().getAppUser().getId();
        System.out.println(id);
        userService.verifyUser(id);
    }
}
