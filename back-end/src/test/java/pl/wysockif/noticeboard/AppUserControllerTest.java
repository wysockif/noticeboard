package pl.wysockif.noticeboard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.appuser.dto.requests.PostUserRequest;
import pl.wysockif.noticeboard.appuser.entity.AppUser;
import pl.wysockif.noticeboard.appuser.repository.AppUserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

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

    private PostUserRequest createValidPostUserRequest() {
        PostUserRequest postUserRequest = new PostUserRequest();
        postUserRequest.setUsername("test-username");
        postUserRequest.setEmail("test@email.com");
        postUserRequest.setFirstName("Firstname");
        postUserRequest.setLastName("Lastname");
        postUserRequest.setPassword("Password123");
        return postUserRequest;
    }

    @Test
    public void postAppUser_whenAppUserIsValid_receiveCreatedStatus() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    public void postAppUser_whenAppUserIsValid_userSavedToDatabase() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        // when
        testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void postAppUser_whenAppUserIsValid_receiveSavedUserId() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        // when
        ResponseEntity<Long> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Long.class);
        // then
        assertThat(response.getBody()).isEqualTo(1L);
    }

    @Test
    public void postUser_whenUserIsValid_passwordIsHashedInDatabase() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        // when
        testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Long.class);
        // then
        List<AppUser> users = userRepository.findAll();
        AppUser userInDatabase = users.get(0);
        assertThat(userInDatabase.getPassword()).isNotEqualTo(postUserRequest.getPassword());
    }


}
