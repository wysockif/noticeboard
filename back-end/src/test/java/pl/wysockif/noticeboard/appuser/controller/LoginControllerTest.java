package pl.wysockif.noticeboard.appuser.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.appuser.dto.requests.PostUserRequest;
import pl.wysockif.noticeboard.appuser.entity.AppUser;
import pl.wysockif.noticeboard.appuser.repository.AppUserRepository;
import pl.wysockif.noticeboard.appuser.service.AppUserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class LoginControllerTest {

    public static final String LOGIN_PATH = "/api/1.0/login";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AppUserService userService;

    @Before
    public void setUp() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
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
        userService.save(user);
        authenticateUser(user.getUsername(), user.getPassword());
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(LOGIN_PATH, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
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