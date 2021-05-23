package pl.wysockif.noticeboard.token;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.SmtpServerRule;
import pl.wysockif.noticeboard.TestUtils;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.token.VerificationToken;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.token.VerificationTokenRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SuppressWarnings("ResultOfMethodCallIgnored")
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class VerifyTokenTest {

    private final int fifteenMinutesInSeconds = 15 * 60;

    private static final String TOKEN_URL = "/api/1.0/tokens";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private AppUserService userService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule(2525);

    @Before
    public void setUp() {
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        tokenRepository.deleteAll();
        noticeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void verifyToken_whenTokenExistsAndHasNotExpired_receiveOkStatus() {
        // given
        createUserWithLockedAccount();
        // when
        VerificationToken token = tokenRepository.findAll().get(0);
        String url = TOKEN_URL + "/" + token.getValue();
        ResponseEntity<Object> response = testRestTemplate.postForEntity(url, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void verifyToken_whenTokenExistsAndHasNotExpired_tokenIsDeletedFromDb() {
        // given
        createUserWithLockedAccount();
        // when
        VerificationToken token = tokenRepository.findAll().get(0);
        String url = TOKEN_URL + "/" + token.getValue();
        testRestTemplate.postForEntity(url, null, Object.class);
        // then
        assertThat(tokenRepository.count()).isEqualTo(0);
    }

    @Test
    public void verifyToken_whenTokenExistsAndHasNotExpired_userAccountIsActivated() {
        // given
        Long userId = createUserWithLockedAccount();
        // when
        VerificationToken token = tokenRepository.findAll().get(0);
        String url = TOKEN_URL + "/" + token.getValue();
        testRestTemplate.postForEntity(url, null, Object.class);
        // then
        Optional<AppUser> user = userRepository.findById(userId);
        boolean isUserAccountLocked = user.isPresent() && user.get().isLockedAccount();
        assertThat(isUserAccountLocked).isFalse();
    }

    @Test
    public void verifyToken_whenTokenDoesNotExist_userAccountIsNotActivated() {
        // given
        Long userId = createUserWithLockedAccount();
        // when
        tokenRepository.findAll().get(0);
        String url = TOKEN_URL + "/" + userId + "-" + UUID.randomUUID();
        testRestTemplate.postForEntity(url, null, Object.class);
        // then
        Optional<AppUser> user = userRepository.findById(userId);
        boolean isUserAccountLocked = user.isPresent() && user.get().isLockedAccount();
        assertThat(isUserAccountLocked).isTrue();
    }


    @Test
    public void verifyToken_whenTokenExistsAndHasExpired_receiveUnauthorizedStatus() {
        // given
        createUserWithLockedAccount();
        // when
        VerificationToken token = tokenRepository.findAll().get(0);
        long generatedAt = token.getGeneratedAt().getTime() - SECONDS.toMillis(fifteenMinutesInSeconds + 1);
        token.setGeneratedAt(new Date(generatedAt));
        VerificationToken expiredToken = tokenRepository.save(token);
        String url = TOKEN_URL + "/" + expiredToken.getValue();
        ResponseEntity<Object> response = testRestTemplate.postForEntity(url, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void verifyToken_whenTokenExistsLessThanFifteenMinutes_receiveOkStatus() {
        // given
        createUserWithLockedAccount();
        // when
        VerificationToken token = tokenRepository.findAll().get(0);
        long generatedAt = token.getGeneratedAt().getTime() - SECONDS.toMillis(fifteenMinutesInSeconds - 10);
        token.setGeneratedAt(new Date(generatedAt));
        VerificationToken expiredToken = tokenRepository.save(token);
        String url = TOKEN_URL + "/" + expiredToken.getValue();
        ResponseEntity<Object> response = testRestTemplate.postForEntity(url, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void verifyToken_whenTokenDoesNotExist_receiveBadRequestStatus() {
        // given
        Long userId = createUserWithLockedAccount();
        // when
        String url = TOKEN_URL + "/" + userId + "-" + UUID.randomUUID();
        ResponseEntity<Object> response = testRestTemplate.postForEntity(url, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void verifyToken_whenTokenIsNotProvidedInUrl_receiveNotFoundStatus() {
        // given
        createUserWithLockedAccount();
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(TOKEN_URL, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void verifyToken_whenTokenIsUsedAndUserSendsAnotherRequest_receiveBadRequestStatus() {
        // given
        createUserWithLockedAccount();
        // when
        VerificationToken token = tokenRepository.findAll().get(0);
        String url = TOKEN_URL + "/" + token.getValue();
        testRestTemplate.postForEntity(url, null, Object.class);
        ResponseEntity<Object> response = testRestTemplate.postForEntity(url, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    private Long createUserWithLockedAccount() {
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long userId = userService.saveUser(user);
        AppUser appUser = userRepository.findById(userId).get();
        appUser.setLockedAccount(true);
        userRepository.save(appUser);
        return userId;
    }
}
