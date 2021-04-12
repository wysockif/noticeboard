package pl.wysockif.noticeboard.appuser.controller;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class LoginControllerTest {

    public static final String LOGIN_PATH = "/api/1.0/login";

    @Autowired
    private TestRestTemplate testRestTemplate;

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
        authorizeUser("test-username", "Password123");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(LOGIN_PATH, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void login_withIncorrectUsernameAndPassword_receiveApiErrorWithoutValidationErrors() {
        // given
        authorizeUser("test-username", "Password123");
        // when
        ResponseEntity<String> response = testRestTemplate.postForEntity(LOGIN_PATH, null, String.class);
        // then
        assertThat(response.getBody().contains("validationErrors")).isFalse();
    }

    private void authorizeUser(String username, String password) {
        testRestTemplate.getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor(username, password));
    }
}