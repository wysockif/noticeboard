package pl.wysockif.noticeboard.user;


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
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.token.VerificationTokenRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.user.AppUserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ActivateAccountTest {
    private static final String USERS_URL = "/api/1.0/users";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AppUserService userService;

    @Autowired
    private NoticeRepository noticeRepository;

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
    public void activateAccount_whenUserAccountIsNotActivated_receiveOkStatus() {
        // given
        AppUser userWithLockedAccount = createUserWithLockedAccount();
        // when
        String url = USERS_URL + "/" + userWithLockedAccount.getEmail() + "/activation";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void activateAccount_whenUserAccountIsNotActivated_tokenIsGenerated() {
        // given
        AppUser userWithLockedAccount = createUserWithLockedAccount();
        long countBefore = tokenRepository.count();
        // when
        String url = USERS_URL + "/" + userWithLockedAccount.getEmail() + "/activation";
        testRestTemplate.exchange(url, PATCH, null, Object.class);
        // then
        long countAfter = tokenRepository.count();
        assertThat(countAfter - countBefore).isEqualTo(1);
    }

    @Test
    public void activateAccount_whenUserAccountIsActivated_receiveBadRequestStatus() {
        // given
        AppUser user = createUserWithLockedAccount();
        user.setLockedAccount(false);
        userRepository.save(user);
        // when
        String url = USERS_URL + "/" + user.getEmail() + "/activation";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void activateAccount_whenUserWithProvidedEmailDoesNotExist_receiveNotFoundStatus() {
        // given
        String nonExistingEmail = "non-existing@email.com";
        // when
        String url = USERS_URL + "/" + nonExistingEmail + "/activation";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }


    private AppUser createUserWithLockedAccount() {
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long userId = userService.saveUser(user);
        AppUser appUser = userRepository.findById(userId).get();
        appUser.setLockedAccount(true);
        return userRepository.save(appUser);
    }
}
