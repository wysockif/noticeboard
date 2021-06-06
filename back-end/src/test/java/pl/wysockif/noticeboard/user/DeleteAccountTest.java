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
import pl.wysockif.noticeboard.dto.user.requests.DeleteAccountRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.notice.Notice;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.ApiError;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.token.VerificationTokenRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.notice.NoticeService;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class DeleteAccountTest {
    private static final String USERS_URL = "/api/1.0/users";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AppUserService userService;

    @Autowired
    private NoticeService noticeService;

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
    public void deleteAccount_whenUserIsUnauthorized_receiveUnauthorizedStatus() {
        // given
        String id = "10";
        // when
        String url = USERS_URL + "/" + id;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, DELETE, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void deleteAccount_whenUserIsAuthorized_receiveOkStatus() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        String id = String.valueOf(currentUserId);
        DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
        deleteAccountRequest.setPassword(user.getPassword());
        HttpEntity<DeleteAccountRequest> requestHttpEntity = new HttpEntity<>(deleteAccountRequest);
        // when
        String url = USERS_URL + "/" + id;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, DELETE, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void deleteAccount_whenUserIsAuthorized_accountDeleted() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        String id = String.valueOf(currentUserId);
        DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
        deleteAccountRequest.setPassword(user.getPassword());
        HttpEntity<DeleteAccountRequest> requestHttpEntity = new HttpEntity<>(deleteAccountRequest);
        // when
        String url = USERS_URL + "/" + id;
        testRestTemplate.exchange(url, DELETE, requestHttpEntity, Object.class);
        // then
        Optional<AppUser> updatedUser = userRepository.findById(currentUserId);
        assertThat(updatedUser.isEmpty()).isTrue();
    }

    @Test
    public void deleteAccount_whenUserIsAuthorizedAndUserHasNotice_receiveOkStatus() throws IOException {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        AppUser creator = userRepository.getOne(currentUserId);
        Long savedNoticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
        String id = String.valueOf(currentUserId);
        DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
        deleteAccountRequest.setPassword(user.getPassword());
        HttpEntity<DeleteAccountRequest> requestHttpEntity = new HttpEntity<>(deleteAccountRequest);
        // when
        String url = USERS_URL + "/" + id;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, DELETE, requestHttpEntity, Object.class);
        // then
        List<Notice> noticesOfThatUser = noticeRepository.findAllByCreatorId(currentUserId);
        assertThat(noticesOfThatUser.size()).isEqualTo(0);
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }


    @Test
    public void deleteAccount_whenUserIsAuthorizedAndUserHasNotice_accountAndNoticeDeleted() throws IOException {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        AppUser creator = userRepository.getOne(currentUserId);
        Long savedNoticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
        String id = String.valueOf(currentUserId);
        DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
        deleteAccountRequest.setPassword(user.getPassword());
        HttpEntity<DeleteAccountRequest> requestHttpEntity = new HttpEntity<>(deleteAccountRequest);
        // when
        String url = USERS_URL + "/" + id;
        testRestTemplate.exchange(url, DELETE, requestHttpEntity, Object.class);
        // then
        Optional<AppUser> updatedUser = userRepository.findById(currentUserId);
        assertThat(updatedUser.isEmpty()).isTrue();
        List<Notice> noticesOfThatUser = noticeRepository.findAllByCreatorId(currentUserId);
        assertThat(noticesOfThatUser.size()).isEqualTo(0);
    }

    @Test
    public void deleteAccount_whenUserIsAuthorizedButPasswordIsIncorrect_receiveUnauthorizedStatus() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
        deleteAccountRequest.setPassword(user.getPassword() + "123");
        HttpEntity<DeleteAccountRequest> requestHttpEntity = new HttpEntity<>(deleteAccountRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, DELETE, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void deleteAccount_whenUserIsDeletingAnotherUserAccount_receiveForbiddenStatus() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
        deleteAccountRequest.setPassword(user.getPassword());
        HttpEntity<DeleteAccountRequest> requestHttpEntity = new HttpEntity<>(deleteAccountRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/123" + id;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, DELETE, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    public void deleteAccount_whenPasswordIsNull_receiveBadRequest() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
        deleteAccountRequest.setPassword(null);
        HttpEntity<DeleteAccountRequest> requestHttpEntity = new HttpEntity<>(deleteAccountRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, DELETE, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void deleteAccount_whenNewPasswordIsNull_receiveApiErrorMessage() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
        deleteAccountRequest.setPassword(null);
        HttpEntity<DeleteAccountRequest> requestHttpEntity = new HttpEntity<>(deleteAccountRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, DELETE, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("password")).isEqualTo("To pole nie może być puste");
    }
}
