package pl.wysockif.noticeboard.controllers.notice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import pl.wysockif.noticeboard.TestUtils;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.notice.Notice;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.ApiError;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PostNoticeTest {

    private static final String NOTICES_URL = "/api/1.0/notices";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AppUserService userService;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Before
    public void setUp() {
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        noticeRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeIsValid_receiveCreatedStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeIsValid_noticeSavedToDatabase() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        // when
        testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(noticeRepository.count()).isEqualTo(1);
    }

    @Test
    public void postNotice_whenUserIsUnauthorizedAndNoticeIsValid_receiveUnauthorizedStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void postNotice_whenUserIsUnauthorizedAndNoticeIsValid_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getMessage()).isEqualTo("Brak autoryzacji");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsNull_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setTitle(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsNull_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setTitle(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("title")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsTooShort_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setTitle("6chars");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsTooLong_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setTitle("more than 60 characters" + TestUtils.generateLongString(38));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleLengthIsIncorrect_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setTitle("more than 60 characters" + TestUtils.generateLongString(38));
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("title"))
                .isEqualTo("Musi mieć conajmniej 8 i conajwyżej 60 znaków");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsNull_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setDescription(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsNull_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setDescription(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("description"))
                .isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsTooShort_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setDescription("less than 60 chars" + TestUtils.generateLongString(40));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionLengthIsIncorrect_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setDescription("less than 60 chars" + TestUtils.generateLongString(40));
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("description"))
                .isEqualTo("Musi mieć conajmniej 60 i conajwyżej 2000 znaków");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsLongerThan255Chars_doesNotThrowJdbcSQLDataException() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setDescription("more than 255 characters" + TestUtils.generateLongString(240));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isNotEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsBelowTheLimit_receiveCreatedStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setDescription("2000 characters" + TestUtils.generateLongString(1985));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsTooLong_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setDescription("more than 2000 characters" + TestUtils.generateLongString(1990));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsNull_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setLocation(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsNull_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setLocation(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("location")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsTooShort_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setLocation("2c");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsTooLong_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setLocation("more than 64 characters" + TestUtils.generateLongString(42));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationLengthIsIncorrect_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setLocation("2c");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("location"))
                .isEqualTo("Musi mieć conajmniej 3 i conajwyżej 60 znaków");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsNull_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setPrice(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsNull_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setPrice(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("price")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsTooLong_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setPrice("10000000.00");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsTooLong_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setPrice("10000000.00");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("price"))
                .isEqualTo("Musi mieć conajmniej 1 i conajwyżej 10 znaków");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePrimaryImageIsNull_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setPrimaryImage(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePrimaryImageIsNull_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setPrimaryImage(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("primaryImage")).isEqualTo("Zdjęcie nie zostało wybrane");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeSecondaryImageIsNull_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setSecondaryImage(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeSecondaryImageIsNull_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setSecondaryImage(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("secondaryImage")).isEqualTo("Zdjęcie nie zostało wybrane");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTertiaryImageIsNull_receiveBadRequestStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setTertiaryImage(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTertiaryImageIsNull_receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        postNoticeRequest.setTertiaryImage(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("tertiaryImage")).isEqualTo("Zdjęcie nie zostało wybrane");
    }

    @Test
    @Transactional
    public void postNotice_whenUserIsAuthorizedAndNoticeIsValid_creatorHasConnectionToNotice() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        // when
        testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        TestTransaction.start();
        AppUser creator = userRepository.findByUsername(username);
        Notice savedNotice = noticeRepository.findAll().get(0);
        assertThat(creator.getNotices().get(0)).isEqualTo(savedNotice);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeIsValid_savedNoticeHasConnectionToCreator() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.saveUser(validPostUserRequest);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = TestUtils.createValidPostNoticeRequest();
        // when
        testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        Notice savedNotice = noticeRepository.findAll().get(0);
        assertThat(savedNotice.getCreator().getUsername()).isEqualTo(username);
    }

}
