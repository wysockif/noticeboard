package pl.wysockif.noticeboard.controllers.notice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.errors.ApiError;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class NoticeControllerTest {

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
    public void postNotice_whenUserIsAuthorizedAndNoticeIsValid_receiveCreatedStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeIsValid_noticeSavedToDatabase() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        // when
        testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(noticeRepository.count()).isEqualTo(1);
    }

    @Test
    public void postNotice_whenUserIsUnauthorizedAndNoticeIsValid_receiveUnauthorizedStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void postNotice_whenUserIsUnauthorizedAndNoticeIsValid_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getMessage()).isEqualTo("Brak autoryzacji");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsNull_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setTitle(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsNull_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setTitle(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("title")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsTooShort_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setTitle("6chars");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsTooLong_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setTitle("more than 60 characters" + generateLongString(38));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleLengthIsIncorrect_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setTitle("more than 60 characters" + generateLongString(38));
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("title"))
                .isEqualTo("Musi mieć conajmniej 8 i conajwyżej 60 znaków");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsNull_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setDescription(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsNull_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setDescription(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("description"))
                .isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsTooShort_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setDescription("less than 60 chars" + generateLongString(40));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionLengthIsIncorrect_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setDescription("less than 60 chars" + generateLongString(40));
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("description"))
                .isEqualTo("Musi mieć conajmniej 60 i conajwyżej 2000 znaków");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsLongerThan255Chars_doesNotThrowJdbcSQLDataException() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setDescription("more than 255 characters" + generateLongString(240));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isNotEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsBelowTheLimit_receiveCreatedStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setDescription("2000 characters" + generateLongString(1985));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsTooLong_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setDescription("more than 2000 characters" + generateLongString(1990));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsNull_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setLocation(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsNull_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setLocation(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("location")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsTooShort_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setLocation("2c");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsTooLong_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setLocation("more than 64 characters" + generateLongString(42));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationLengthIsIncorrect_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setLocation("2c");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("location"))
                .isEqualTo("Musi mieć conajmniej 3 i conajwyżej 60 znaków");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsNull_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setPrice(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsNull_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setPrice(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("price")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsTooLong_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setPrice("10000000.00");
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsTooLong_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setPrice("10000000.00");
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("price"))
                .isEqualTo("Musi mieć conajmniej 1 i conajwyżej 10 znaków");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePrimaryImageIsNull_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setPrimaryImage(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticePrimaryImageIsNull_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setPrimaryImage(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("primaryImage")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeSecondaryImageIsNull_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setSecondaryImage(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeSecondaryImageIsNull_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setSecondaryImage(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("secondaryImage")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTertiaryImageIsNull_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setTertiaryImage(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTertiaryImageIsNull_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setTertiaryImage(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("tertiaryImage")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListIsNull_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setKeywords(null);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListIsNull_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setKeywords(null);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("keywords")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListIsTooShort_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setKeywords(List.of("key1", "key2"));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListIsTooLong_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        List<String> keywords = List.of("key1", "key2", "key3", "key4", "key5", "key6", "key7",
                "key8", "key9", "key10", "key11", "key12", "key13");
        postNoticeRequest.setKeywords(keywords);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListSizeIsIncorrect_receiveApiError() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setKeywords(List.of("key1", "key2"));
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        System.out.println(response);
        // then
        assertThat(response.getBody().getValidationErrors().get("keywords"))
                .isEqualTo("Musi być conajmniej 3 i conajwyżej 12 słów kluczy");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListItemIsTooShort_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        List<String> keywords = List.of("key1", "2", "key3");
        postNoticeRequest.setKeywords(keywords);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListItemIsTooShort_receiveApiErrorMessage() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        List<String> keywords = List.of("key1", "2", "key3");
        postNoticeRequest.setKeywords(keywords);
        // when
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, ApiError.class);
        System.out.println(response);
        // then
        assertThat(response.getBody().getValidationErrors().get("keywords[1]"))
                .isEqualTo("Każde słowo-klucz musi mieć conajmniej 3 i conajwyżej 30 znaków");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListItemIsTooLong_receiveBadRequestStatus() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        List<String> keywords = List.of("key1", "key2", generateLongString(31));
        postNoticeRequest.setKeywords(keywords);
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    private PostNoticeRequest createValidPostNoticeRequest() {
        String noticeDescription = "Notice description " + generateLongString(60);
        PostNoticeRequest postNoticeRequest = new PostNoticeRequest();
        postNoticeRequest.setTitle("Notice title");
        postNoticeRequest.setDescription(noticeDescription);
        postNoticeRequest.setPrice("12.23");
        postNoticeRequest.setLocation("Notice Location");
        postNoticeRequest.setPrimaryImage("NoticePrimaryImage.png");
        postNoticeRequest.setSecondaryImage("NoticeSecondaryImage.png");
        postNoticeRequest.setTertiaryImage("NoticeTertiaryImage.png");
        postNoticeRequest.setKeywords(List.of("Key1", "Key2", "Key3"));
        return postNoticeRequest;
    }

    private PostUserRequest createValidPostUserRequest(String username) {
        PostUserRequest postUserRequest = new PostUserRequest();
        postUserRequest.setUsername(username);
        postUserRequest.setEmail("usermail@email.com");
        postUserRequest.setFirstName("Firstname");
        postUserRequest.setLastName("Lastname");
        postUserRequest.setPassword("Password123");
        return postUserRequest;
    }

    private String generateLongString(int length) {
        return new String(new char[length]).replace('\0', 'u');
    }

    private void addAuthenticationInterceptor(PostUserRequest user) {
        testRestTemplate.getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor(user.getUsername(), user.getPassword()));
    }
}