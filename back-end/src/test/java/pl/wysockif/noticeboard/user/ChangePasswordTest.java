package pl.wysockif.noticeboard.user;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.SmtpServerRule;
import pl.wysockif.noticeboard.TestUtils;
import pl.wysockif.noticeboard.dto.user.requests.ChangePasswordRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.ApiError;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.token.VerificationTokenRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ChangePasswordTest {
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public void changePassword_whenUserIsUnauthorized_receiveUnauthorizedStatus() {
        // given
        String id = "10";
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void changePassword_whenUserIsAuthorized_receiveOkStatus() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        String id = String.valueOf(currentUserId);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword("Password321");
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void changePassword_whenUserIsAuthorized_passwordUpdated() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword("Password321");
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "/password";
        testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        Optional<AppUser> updatedUser = userRepository.findById(currentUserId);
        assertThat(passwordEncoder.matches(changePasswordRequest.getNewPassword(),
                updatedUser.get().getPassword())).isTrue();
    }

    @Test
    public void changePassword_whenUserIsAuthorizedButOldPasswordIsIncorrect_receiveUnauthorizedStatus() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(user.getPassword() + "non-matching-password");
        changePasswordRequest.setNewPassword("Password321");
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void changePassword_whenNewPasswordIsTheSameAsOldOne_receiveBadRequestStatus() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword(user.getPassword());
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void changePassword_whenUserIsChangingAnotherUserPassword_receiveForbiddenStatus() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword("Password321");
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "12345/password";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    public void changePassword_whenOldPasswordIsNull_receiveBadRequest() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(null);
        changePasswordRequest.setNewPassword("Password321");
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "12345/password";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void changePassword_whenOldPasswordIsNull_receiveApiErrorMessage() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(null);
        changePasswordRequest.setNewPassword("Password321");
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("oldPassword")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void changePassword_whenNewPasswordIsNull_receiveBadRequest() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword(null);
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "12345/password";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void changePassword_whenNewPasswordIsNull_receiveApiErrorMessage() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword(null);
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("newPassword")).isEqualTo("To pole nie może być puste");
    }


    @Test
    public void changePassword_whenNewPasswordIsToShort_receiveBadRequest() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword("Ab3");
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void changePassword_whenNewPasswordIsTooLong_receiveBadRequest() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        String password = "tooLongPassword" + TestUtils.generateLongString(60);
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword(password);
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void changePassword_whenNewPasswordIsSizeIsIncorrect_receiveErrorMessage() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        String password = "tooLongPassword12" + TestUtils.generateLongString(60);
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword(password);
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("newPassword"))
                .isEqualTo("Musi mieć co najmniej 8 i co najwyżej 60 znaków");
    }


    @Test
    public void changePassword_whenNewPasswordDoesNotMatchThePattern_receiveBadRequest() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword("withoutuppercaseandnumber");
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void changePassword_whenUserHasPasswordNotMatchingThePattern_receiveMessageOfPatternError() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(user.getPassword());
        changePasswordRequest.setNewPassword("withoutuppercaseandnumber");
        HttpEntity<ChangePasswordRequest> requestHttpEntity = new HttpEntity<>(changePasswordRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id + "/password";
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("newPassword"))
                .isEqualTo("Musi mieć co najmniej jedną małą i wielką literę oraz cyfrę");
    }
}
