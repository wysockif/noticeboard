package pl.wysockif.noticeboard.services.token;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wysockif.noticeboard.entities.token.VerificationToken;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.ExpiredTokenException;
import pl.wysockif.noticeboard.errors.token.IncorrectTokenException;
import pl.wysockif.noticeboard.repositories.token.VerificationTokenRepository;
import pl.wysockif.noticeboard.services.mail.MailService;
import pl.wysockif.noticeboard.services.user.AppUserService;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
            throw new IncorrectTokenException("Konto jest już aktywowane albo link jest nieprawidłowy.");
        }
        checkIfTokenHasExpired(token.get().getGeneratedAt(), tokenValue);
        Long userId = token.get().getAppUser().getId();
        userService.unlockUserAccount(userId);
        deleteAllByUserId(userId);
        LOGGER.info("Verified token: " + tokenValue);
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        LOGGER.info("Deleting all token by userId (userId:" + userId + ")");
        tokenRepository.deleteAllByAppUserId(userId);
        LOGGER.info("Deleted all token by userId (userId:" + userId + ")");
    }

    @SneakyThrows
    private void checkIfTokenHasExpired(Date generatedAt, String tokenValue) {
        long diff = new Date().getTime() - generatedAt.getTime();
        if(MILLISECONDS.toSeconds(diff) > 15 * 60){
            LOGGER.info("Token has expired: " + tokenValue);
            throw new ExpiredTokenException("Ten link aktywujący konto wygasł");
        }
    }

    public void sendNewToken(AppUser appUser) {
        LOGGER.info("Creating new token for user: " + appUser.getUsername());
        String tokenValue = saveNewTokenInDatabase(appUser);
        String url = linkToClick + "/" + tokenValue;
        try {
            mailService.sendMail(appUser.getEmail(), appUser.getFirstName(), url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LOGGER.info("Created token for user: " + appUser.getUsername());
    }

    private String saveNewTokenInDatabase(AppUser appUser) {
        VerificationToken token = new VerificationToken();
        String tokenValue = appUser.getId() + "-" + UUID.randomUUID();
        token.setGeneratedAt(new Date());
        token.setValue(tokenValue);
        token.setAppUser(appUser);
        tokenRepository.save(token);
        return tokenValue;
    }
}
