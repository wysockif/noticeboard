package pl.wysockif.noticeboard.services.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.logging.Logger;

@Service
public class VerificationTokenService {

    private final Logger LOGGER = Logger.getLogger(VerificationTokenService.class.getName());

    @Value("${verify-address-email-url}")
    private String linkToClick;

    @Autowired
    private AppUserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Transactional
    public void verifyToken(String tokenValue) {
        LOGGER.info("Verifing token: " + tokenValue);
        Optional<VerificationToken> token = tokenRepository.findByValue(tokenValue);
        if (token.isEmpty()) {
            LOGGER.info("Not valid token: " + tokenValue);
            throw new IncorrectTokenException("Użytkownik został już zweryfikowany albo token znajdujący się w linku jest nieprawidłowy.");
        }
        Long userId = token.get().getAppUser().getId();
        userService.unlockUserAccount(userId);
        tokenRepository.deleteAllByAppUserId(userId);
        LOGGER.info("Verified token: " + tokenValue);
    }

    public void sendNewToken(AppUser appUser) {
        LOGGER.info("Creating new token for user: " + appUser.getUsername());
        String tokenValue = saveNewTokenInDatabase(appUser);
        String url = linkToClick + "/" + tokenValue;
        try {
            mailService.sendMail(appUser.getEmail(), "Zweryfikuj swój adres email", url, false);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LOGGER.info("Created token for user: " + appUser.getUsername());
    }

    private String saveNewTokenInDatabase(AppUser appUser) {
        VerificationToken token = new VerificationToken();
        String tokenValue = appUser.getId() + "-" + UUID.randomUUID();
        token.setValue(tokenValue);
        token.setAppUser(appUser);
        tokenRepository.save(token);
        return tokenValue;
    }
}
