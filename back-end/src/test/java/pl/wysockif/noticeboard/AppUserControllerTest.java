package pl.wysockif.noticeboard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.appuser.entity.AppUser;
import pl.wysockif.noticeboard.appuser.repository.AppUserRepository;
import pl.wysockif.noticeboard.shared.Response;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppUserControllerTest {
    private static final String API_1_0_USERS = "/api/1.0/users";

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AppUserRepository userRepository;

    @Before
    public void setUp() {
        userRepository.deleteAll();
    }

    private AppUser createValidUser() {
        AppUser appUser = new AppUser();
        appUser.setUsername("test-username");
        appUser.setEmail("test@email.com");
        appUser.setDisplayName("test-display-name");
        appUser.setPassword("Password123");
        return appUser;
    }

    @Test
    public void postAppUser_whenAppUserIsValid_receiveOk() {
        // given
        AppUser appUser = createValidUser();
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, appUser, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postAppUser_whenAppUserIsValid_userSavedToDatabase() {
        // given
        AppUser appUser = createValidUser();
        // when
        testRestTemplate.postForEntity(API_1_0_USERS, appUser, Object.class);
        // then
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void postAppUser_whenAppUserIsValid_receiveSuccessMessage() {
        // given
        AppUser appUser = createValidUser();
        // when
        ResponseEntity<Response> response = testRestTemplate.postForEntity(API_1_0_USERS, appUser, Response.class);
        // then
        assertThat(response.getBody().getMessage()).isNotNull();
    }


}
