package pl.wysockif.noticeboard.controllers.user;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.SmtpServerRule;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.token.VerificationTokenRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class LoginUserTest {

    public static final String LOGIN_PATH = "/api/1.0/login";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AppUserService userService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private NoticeRepository noticeRepository;

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
    public void login_withoutUsernameAndPassword_receiveUnauthorized() {
        // given
        Object request = null;
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(LOGIN_PATH, request, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void login_withIncorrectUsernameAndPassword_receiveUnauthorized() {
        // given
        authenticateUser("test-username", "Password123");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(LOGIN_PATH, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void login_withIncorrectUsernameAndPassword_receiveApiErrorWithoutValidationErrors() {
        // given
        authenticateUser("test-username2", "Password1");
        // when
        ResponseEntity<String> response = testRestTemplate.postForEntity(LOGIN_PATH, null, String.class);
        // then
        assertThat(response.getBody().contains("validationErrors")).isFalse();
    }

    @Test
    public void login_withValidUsernameAndPassword_receiveOk() {
        // given
        PostUserRequest user = createValidAppUser();
        userService.saveUser(user);
        authenticateUser(user.getUsername(), user.getPassword());
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(LOGIN_PATH, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void login_withValidUsernameAndPassword_doNotReceiveLoggedInUserPassword() {
        // given
        PostUserRequest user = createValidAppUser();
        userService.saveUser(user);
        authenticateUser(user.getUsername(), user.getPassword());
        // when
        ResponseEntity<Map<String, Object>> response =
                testRestTemplate.exchange(LOGIN_PATH, POST, null, new ParameterizedTypeReference<>() {
                });
        // then
        Boolean isPasswordField = response.getBody().containsKey("password");
        assertThat(isPasswordField).isFalse();
    }

    @Test
    public void login_withValidUsernameAndPassword_receiveLoggedInUserId() {
        // given
        PostUserRequest user = createValidAppUser();
        Long userInDbId = userService.saveUser(user);
        authenticateUser(user.getUsername(), user.getPassword());
        // when
        ResponseEntity<Map<String, Object>> response =
                testRestTemplate.exchange(LOGIN_PATH, POST, null, new ParameterizedTypeReference<>() {
                });
        // then
        Long id = Long.valueOf((Integer) response.getBody().get("id"));
        assertThat(id).isEqualTo(userInDbId);
    }

    @Test
    public void login_withValidUsernameAndPassword_receiveLoggedInUserUsername() {
        // given
        PostUserRequest user = createValidAppUser();
        userService.saveUser(user);
        authenticateUser(user.getUsername(), user.getPassword());
        // when
        ResponseEntity<Map<String, Object>> response =
                testRestTemplate.exchange(LOGIN_PATH, POST, null, new ParameterizedTypeReference<>() {
                });
        // then
        String actualUsername = (String) response.getBody().get("username");
        assertThat(actualUsername).isEqualTo(user.getUsername());
    }

    @Test
    public void login_withValidUsernameAndPassword_receiveLoggedInUserEmail() {
        // given
        PostUserRequest user = createValidAppUser();
        userService.saveUser(user);
        authenticateUser(user.getUsername(), user.getPassword());
        // when
        ResponseEntity<Map<String, Object>> response =
                testRestTemplate.exchange(LOGIN_PATH, POST, null, new ParameterizedTypeReference<>() {
                });
        // then
        String actualEmail = (String) response.getBody().get("email");
        assertThat(actualEmail).isEqualTo(user.getEmail());
    }

    @Test
    public void login_withValidUsernameAndPassword_receiveLoggedInUserFirstName() {
        // given
        PostUserRequest user = createValidAppUser();
        userService.saveUser(user);
        authenticateUser(user.getUsername(), user.getPassword());
        // when
        ResponseEntity<Map<String, Object>> response =
                testRestTemplate.exchange(LOGIN_PATH, POST, null, new ParameterizedTypeReference<>() {
                });
        // then
        String actualFirstName = (String) response.getBody().get("firstName");
        assertThat(actualFirstName).isEqualTo(user.getFirstName());
    }

    @Test
    public void login_withValidUsernameAndPassword_receiveLoggedInUserLastName() {
        // given
        PostUserRequest user = createValidAppUser();
        userService.saveUser(user);
        authenticateUser(user.getUsername(), user.getPassword());
        // when
        ResponseEntity<Map<String, Object>> response =
                testRestTemplate.exchange(LOGIN_PATH, POST, null, new ParameterizedTypeReference<>() {
                });
        // then
        String actualLastName = (String) response.getBody().get("lastName");
        assertThat(actualLastName).isEqualTo(user.getLastName());
    }

    @Test
    public void login_withValidUsernameAndPassword_receiveLoggedInUserImage() {
        // given
        PostUserRequest user = createValidAppUser();
        userService.saveUser(user);
        authenticateUser(user.getUsername(), user.getPassword());
        // when
        ResponseEntity<Map<String, Object>> response =
                testRestTemplate.exchange(LOGIN_PATH, POST, null, new ParameterizedTypeReference<>() {
                });
        // then
        Boolean isImageField = response.getBody().containsKey("image");
        assertThat(isImageField).isTrue();
    }


    private PostUserRequest createValidAppUser() {
        PostUserRequest user = new PostUserRequest();
        user.setFirstName("Firstname");
        user.setLastName("Lastname");
        user.setEmail("email@email.com");
        user.setPassword("Password123");
        user.setUsername("test-username");
        return user;
    }

    private void authenticateUser(String username, String password) {
        testRestTemplate.getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor(username, password));
    }
}