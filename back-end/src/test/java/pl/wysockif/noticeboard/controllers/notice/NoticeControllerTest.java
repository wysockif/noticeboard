package pl.wysockif.noticeboard.controllers.notice;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import pl.wysockif.noticeboard.controllers.TestPage;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.notice.Notice;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.ApiError;
import pl.wysockif.noticeboard.mappers.notice.NoticeMapper;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.notice.NoticeService;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
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

    @Autowired
    private NoticeService noticeService;

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
    public void postNotice_whenUserIsAuthorizedAndNoticeIsValid_noticeSavedToDatabase() throws IOException {
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
    public void postNotice_whenUserIsUnauthorizedAndNoticeIsValid_receiveUnauthorizedStatus() throws IOException {
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
    public void postNotice_whenUserIsUnauthorizedAndNoticeIsValid_receiveApiErrorMessage() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsNull_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsNull_receiveApiErrorMessage() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsTooShort_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleIsTooLong_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeTitleLengthIsIncorrect_receiveApiErrorMessage() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsNull_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsNull_receiveApiErrorMessage() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsTooShort_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionLengthIsIncorrect_receiveApiErrorMessage() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsLongerThan255Chars_doesNotThrowJdbcSQLDataException() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsBelowTheLimit_receiveCreatedStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsTooLong_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsNull_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsNull_receiveApiErrorMessage() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsTooShort_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationIsTooLong_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeLocationLengthIsIncorrect_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsNull_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsNull_receiveApiErrorMessage() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsTooLong_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticePriceIsTooLong_receiveApiErrorMessage() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticePrimaryImageIsNull_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticePrimaryImageIsNull_receiveApiErrorMessage() throws IOException {
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
        assertThat(response.getBody().getValidationErrors().get("primaryImage")).isEqualTo("Zdjęcie nie zostało wybrane");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeSecondaryImageIsNull_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeSecondaryImageIsNull_receiveApiErrorMessage() throws IOException {
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
        assertThat(response.getBody().getValidationErrors().get("secondaryImage")).isEqualTo("Zdjęcie nie zostało wybrane");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeTertiaryImageIsNull_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeTertiaryImageIsNull_receiveApiErrorMessage() throws IOException {
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
        assertThat(response.getBody().getValidationErrors().get("tertiaryImage")).isEqualTo("Zdjęcie nie zostało wybrane");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListIsNull_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListIsNull_receiveApiErrorMessage() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListIsTooShort_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListIsTooLong_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListSizeIsIncorrect_receiveApiError() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListItemIsTooShort_receiveBadRequestStatus() throws IOException {
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
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListItemIsTooShort_receiveApiErrorMessage() throws IOException {
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
                .isEqualTo("Każde słowo-klucz musi mieć conajmniej 3 i conajwyżej 20 znaków");
    }

    @Test
    public void postNotice_whenUserIsAuthorizedAndNoticeKeywordsListItemIsTooLong_receiveBadRequestStatus() throws IOException {
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

    @Test
    @Transactional
    public void postNotice_whenUserIsAuthorizedAndNoticeIsValid_creatorHasConnectionToNotice() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        TestTransaction.flagForCommit();
        TestTransaction.end();
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
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
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        // when
        testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        Notice savedNotice = noticeRepository.findAll().get(0);
        assertThat(savedNotice.getCreator().getUsername()).isEqualTo(username);
    }

    @Test
    public void getNotices_whenThereAreNoNoticesInDatabase_receiveOkStatus() {
        // given
        // when
        ResponseEntity<Object> response = testRestTemplate.getForEntity(NOTICES_URL, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void getNoticeById_whenThereIsNoticeWithProvidedId_receiveOkStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        Long savedNoticeId = noticeService.save(createValidPostNoticeRequest(), creator);
        // when
        String url = NOTICES_URL + '/' + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.getForEntity(url, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void getNoticeById_whenThereIsNotNoticeWithProvidedId_receiveNotFoundStatus() {
        // given
        String nonExistingNoticeId = "123";
        // when
        String url = NOTICES_URL + '/' + nonExistingNoticeId;
        ResponseEntity<Object> response = testRestTemplate.getForEntity(url, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }


    @Test
    public void getNotices_whenThereAreNoNoticesInDatabase_receiveEmptyPage() {
        // given
        // when
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(NOTICES_URL, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    public void getNotices_whenThereIsANoticeInDatabase_receivePageWithOneNotice() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        noticeService.save(createValidPostNoticeRequest(), creator);
        // when
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(NOTICES_URL, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    public void getNotices_whenThereIs15NoticesInDatabaseAndRequestedSizeIsEqual10_receivePageWith10Notices() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        saveNValidNotices(creator, 15);
        // when
        String urlFor10Notices = NOTICES_URL + "?size=12";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(urlFor10Notices, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(12);
    }

    @Test
    public void getNotices_whenThereIsARequestParamUserId_receivePageWithNoticesOfThatUser() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        saveNValidNotices(creator, 15);
        // when
        String urlFor10Notices = NOTICES_URL + "?size=12";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(urlFor10Notices, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(12);
    }

    @Test
    public void getNotices_whenThereIs15NoticesInDatabaseAndRequestedSizeIsNotGiven_receivePageWith10Notices() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        saveNValidNotices(creator, 20);
        // when
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(NOTICES_URL, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(18);
    }

    @Test
    @Transactional
    public void getNotices_whenUserUsernameIsProvidedInUrl_receiveNoticesOfThatUser() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = createValidPostUserRequest("first-username");
        userService.save(firstValidPostUserRequest);
        Long firstCreatorId = userService.save(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNotices(firstCreator, 5);
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        PostUserRequest secondValidPostUserRequest = createValidPostUserRequest("second-username");
        userService.save(secondValidPostUserRequest);
        Long secondCreatorId = userService.save(secondValidPostUserRequest);
        AppUser secondCreator = userRepository.getOne(secondCreatorId);
        saveNValidNotices(secondCreator, 3);
        TestTransaction.flagForCommit();
        TestTransaction.end();
        // when
        String url = NOTICES_URL + "?username=" + secondCreator.getUsername();
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void getNotices_whenUserUsernameIsProvidedInUrlButThisUserDoesNotExist_receiveZeroNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = createValidPostUserRequest("first-username");
        userService.save(firstValidPostUserRequest);
        Long firstCreatorId = userService.save(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNotices(firstCreator, 5);
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        TestTransaction.flagForCommit();
        TestTransaction.end();
        // when
        String url = NOTICES_URL + "?username=" + firstCreator.getUsername() + "-non-existing";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(0);
    }


    private PostNoticeRequest createValidPostNoticeRequest() throws IOException {
        ClassPathResource imageResource = new ClassPathResource("default-notice-image.jpg");
        byte[] imageArr = FileUtils.readFileToByteArray(imageResource.getFile());
        String imageAsBase64 = Base64.getEncoder().encodeToString(imageArr);
        String noticeDescription = "Notice description " + generateLongString(60);
        PostNoticeRequest postNoticeRequest = new PostNoticeRequest();
        postNoticeRequest.setTitle("Notice title");
        postNoticeRequest.setDescription(noticeDescription);
        postNoticeRequest.setPrice("12.23");
        postNoticeRequest.setLocation("Notice Location");
        postNoticeRequest.setPrimaryImage(imageAsBase64);
        postNoticeRequest.setSecondaryImage(imageAsBase64);
        postNoticeRequest.setTertiaryImage(imageAsBase64);
        postNoticeRequest.setKeywords(List.of("Key1", "Key2", "Key3"));
        return postNoticeRequest;
    }

    private void saveNValidNotices(AppUser creator, int n) throws IOException {
        for (int i = 0; i < n; i++) {
            PostNoticeRequest validNotice = createValidPostNoticeRequest();
            noticeService.save(validNotice, creator);
        }
    }

    private Notice createValidNotice() throws IOException {
        return NoticeMapper.INSTANCE.postNoticeRequestToNotice(createValidPostNoticeRequest());
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