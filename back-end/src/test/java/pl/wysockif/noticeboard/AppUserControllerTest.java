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
import pl.wysockif.noticeboard.appuser.mapper.AppUserMapper;
import pl.wysockif.noticeboard.appuser.repository.AppUserRepository;
import pl.wysockif.noticeboard.error.ApiError;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    private PostUserRequest createValidPostUserRequest() {
        PostUserRequest postUserRequest = new PostUserRequest();
        postUserRequest.setUsername("username");
        postUserRequest.setEmail("usermail@email.com");
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
        assertThat(response.getBody()).isNotNegative();
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

    @Test
    public void postUser_whenUserHasNullUsername_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setUsername(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveMessageOfNullField() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setUsername(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("username")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postUser_whenUserHasTooShortUsername_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setUsername("abc");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooLongUsername_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        String username = "tooLongUsername" + generateLongString(64);
        postUserRequest.setUsername(username);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenAnotherUserHasUsedUsername_receiveBadRequest() {
        // given
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(createValidPostUserRequest());
        userRepository.save(user);
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setEmail("sameUserNameButDifferentEmail@mail.com");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenAnotherUserHasUsedUsername_receiveMessageOfUsedUsername() {
        // given
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(createValidPostUserRequest());
        userRepository.save(user);
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setEmail("sameUserNameButDifferentEmail@mail.com");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("username"))
                .isEqualTo("Podana nazwa użytkownika jest już zajęta");
    }

    @Test
    public void postUser_whenUserHasNullFirstName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setFirstName(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullFirstName_receiveMessageOfNullField() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setFirstName(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("firstName")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postUser_whenUserHasTooShortFirstName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setFirstName("Im");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooShortFirstName_receiveMessageOfTooShortTextInField() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setFirstName("Im");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("firstName"))
                .isEqualTo("Musi mieć conajmniej 3 i conajwyżej 64 znaki");
    }

    @Test
    public void postUser_whenUserHasTooLongFirstName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        String firstName = "tooLongFirstName" + generateLongString(60);
        postUserRequest.setFirstName(firstName);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullLastName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setLastName(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullLastName_receiveMessageOfNullField() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setLastName(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("lastName")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postUser_whenUserHasTooShortLastName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setLastName("Im");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooLongLastName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        String lastName = "tooLongLastName" + generateLongString(60);
        postUserRequest.setLastName(lastName);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullEmail_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setEmail(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullEmail_receiveMessageOfNullField() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setEmail(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("email")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postUser_whenUserHasNotValidEmail_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setEmail("notValidEmail");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooShortEmail_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setEmail("a@a");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooLongEmail_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        String email = "too@long.email" + generateLongString(60);
        postUserRequest.setEmail(email);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenAnotherUserHasUserEmail_receiveBadRequest() {
        // given
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(createValidPostUserRequest());
        userRepository.save(user);
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setUsername("sameEmailButDifferentUsername");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenAnotherUserHasUsedEmail_receiveMessageOfUsedEmail() {
        // given
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(createValidPostUserRequest());
        userRepository.save(user);
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setUsername("sameEmailButDifferentUsername");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("email"))
                .isEqualTo("Podany adres email został już zarejestrowany");
    }

    @Test
    public void postUser_whenUserHasNullPassword_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setPassword(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullPassword_receiveMessageOfNullField() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setPassword(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("password")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postUser_whenUserHasTooShortPassword_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setPassword("Ab3");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooLongPassword_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        String password = "tooLongPassword" + generateLongString(60);
        postUserRequest.setPassword(password);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordNotMatchingThePattern_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setPassword("withoutuppercaseandnumber");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordNotMatchingThePattern_receiveMessageOfPatternError() {
        // given
        PostUserRequest postUserRequest = createValidPostUserRequest();
        postUserRequest.setPassword("withoutuppercaseandnumber");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("password"))
                .isEqualTo("Musi mieć conajmniej jedną małą i wielką literę oraz cyfrę");
    }

    @Test
    public void postUser_whenUserIsNotValid_receiveDedicatedError() {
        // given
        PostUserRequest postUserRequest = new PostUserRequest();
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(API_1_0_USERS, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getUrl()).isEqualTo(API_1_0_USERS);
    }

    // https://stackoverflow.com/questions/2804827/create-a-string-with-n-characters
    private String generateLongString(int length) {
        return new String(new char[length]).replace('\0', 'u');
    }

}
