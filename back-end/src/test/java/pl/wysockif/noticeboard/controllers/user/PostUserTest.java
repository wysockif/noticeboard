package pl.wysockif.noticeboard.controllers.user;


import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.TestUtils;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.ApiError;
import pl.wysockif.noticeboard.mappers.user.AppUserMapper;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PostUserTest {
    private static final String USERS_URL = "/api/1.0/users";

    @Value("${upload-folder-path}")
    private String uploadFolderPath;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Before
    public void setUp() {
        noticeRepository.deleteAll();
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @After
    public void cleanUp() throws IOException {
        FileUtils.cleanDirectory(new File(uploadFolderPath + "/profile-images"));
        FileUtils.cleanDirectory(new File(uploadFolderPath + "/notice-images"));
    }

    @Test
    public void postUser_whenAppUserIsValid_receiveCreatedStatus() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    public void postUser_whenAppUserIsValid_userSavedToDatabase() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        // when
        testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    public void postUser_whenAppUserIsValid_receiveSavedUserId() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        // when
        ResponseEntity<Long> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Long.class);
        // then
        assertThat(response.getBody()).isNotNegative();
    }

    @Test
    public void postUser_whenUserIsValid_passwordIsHashedInDatabase() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        // when
        testRestTemplate.postForEntity(USERS_URL, postUserRequest, Long.class);
        // then
        List<AppUser> users = userRepository.findAll();
        AppUser userInDatabase = users.get(0);
        assertThat(userInDatabase.getPassword()).isNotEqualTo(postUserRequest.getPassword());
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setUsername(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveMessageOfNullField() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setUsername(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("username")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postUser_whenUserHasTooShortUsername_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setUsername("abc");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooLongUsername_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        String username = "tooLongUsername" + TestUtils.generateLongString(64);
        postUserRequest.setUsername(username);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenAnotherUserHasUsedUsername_receiveBadRequest() {
        // given
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(TestUtils.createValidPostUserRequest("username"));
        userRepository.save(user);
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setEmail("sameUserNameButDifferentEmail@mail.com");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenAnotherUserHasUsedUsername_receiveMessageOfUsedUsername() {
        // given
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(TestUtils.createValidPostUserRequest("username"));
        userRepository.save(user);
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setEmail("sameUserNameButDifferentEmail@mail.com");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("username"))
                .isEqualTo("Podana nazwa użytkownika jest już zajęta");
    }

    @Test
    public void postUser_whenUserHasNullFirstName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setFirstName(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullFirstName_receiveMessageOfNullField() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setFirstName(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("firstName")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postUser_whenUserHasTooShortFirstName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setFirstName("Im");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooShortFirstName_receiveMessageOfTooShortTextInField() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setFirstName("Im");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("firstName"))
                .isEqualTo("Musi mieć conajmniej 3 i conajwyżej 60 znaków");
    }

    @Test
    public void postUser_whenUserHasTooLongFirstName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        String firstName = "tooLongFirstName" + TestUtils.generateLongString(60);
        postUserRequest.setFirstName(firstName);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullLastName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setLastName(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullLastName_receiveMessageOfNullField() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setLastName(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("lastName")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postUser_whenUserHasTooShortLastName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setLastName("Im");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooLongLastName_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        String lastName = "tooLongLastName" + TestUtils.generateLongString(60);
        postUserRequest.setLastName(lastName);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullEmail_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setEmail(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullEmail_receiveMessageOfNullField() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setEmail(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("email")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postUser_whenUserHasNotValidEmail_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setEmail("notValidEmail");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooShortEmail_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setEmail("a@a");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooLongEmail_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        String email = "too@long.email" + TestUtils.generateLongString(60);
        postUserRequest.setEmail(email);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenAnotherUserHasUserEmail_receiveBadRequest() {
        // given
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(TestUtils.createValidPostUserRequest("username"));
        userRepository.save(user);
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setUsername("sameEmailButDifferentUsername");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenAnotherUserHasUsedEmail_receiveMessageOfUsedEmail() {
        // given
        AppUser user = AppUserMapper.INSTANCE.postUserRequestToAppUser(TestUtils.createValidPostUserRequest("username"));
        userRepository.save(user);
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setUsername("sameEmailButDifferentUsername");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("email"))
                .isEqualTo("Podany adres email został już zarejestrowany");
    }

    @Test
    public void postUser_whenUserHasNullPassword_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setPassword(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullPassword_receiveMessageOfNullField() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setPassword(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("password")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postUser_whenUserHasTooShortPassword_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setPassword("Ab3");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasTooLongPassword_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        String password = "tooLongPassword" + TestUtils.generateLongString(60);
        postUserRequest.setPassword(password);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordNotMatchingThePattern_receiveBadRequest() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setPassword("withoutuppercaseandnumber");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordNotMatchingThePattern_receiveMessageOfPatternError() {
        // given
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest("username");
        postUserRequest.setPassword("withoutuppercaseandnumber");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("password"))
                .isEqualTo("Musi mieć conajmniej jedną małą i wielką literę oraz cyfrę");
    }

    @Test
    public void postUser_whenUserIsNotValid_receiveDedicatedError() {
        // given
        PostUserRequest postUserRequest = new PostUserRequest();
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(USERS_URL, postUserRequest, ApiError.class);
        // then
        assertThat(response.getBody().getMessage()).isEqualTo("Validation error");
    }
}
