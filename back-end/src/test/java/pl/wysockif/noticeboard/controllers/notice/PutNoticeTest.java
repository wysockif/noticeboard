package pl.wysockif.noticeboard.controllers.notice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.SmtpServerRule;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.dto.notice.requests.PutNoticeRequest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static pl.wysockif.noticeboard.TestUtils.addAuthenticationInterceptor;
import static pl.wysockif.noticeboard.TestUtils.createValidPostNoticeRequest;
import static pl.wysockif.noticeboard.TestUtils.createValidPostUserRequest;
import static pl.wysockif.noticeboard.TestUtils.createValidPutUserRequest;
import static pl.wysockif.noticeboard.TestUtils.generateLongString;
import static pl.wysockif.noticeboard.TestUtils.getImageAsBase64;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PutNoticeTest {

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

    @Autowired
    private VerificationTokenRepository tokenRepository;

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
    public void putNotice_whenUserIsAuthorizedAndNoticeIsValid_receiveOkStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeIsValid_noticeUpdatedInDatabase() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        Optional<Notice> updatedNotice = noticeRepository.findById(savedNoticeId);
        assertThat(updatedNotice.get().getTitle()).isEqualTo(validPutUserRequest.getTitle());
    }

    @Test
    public void putNotice_whenUserIsUnauthorizedAndNoticeIsValid_receiveUnauthorizedStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void putNotice_whenUserIsUnauthorizedAndNoticeIsValid_receiveApiError() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getMessage()).isEqualTo("Brak autoryzacji");
    }

    @Test
    public void putNotice_whenAuthorizedUserUpdatesAnotherUserNoticeAndNoticeIsValid_receiveForbiddenStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        PostUserRequest validPostUserRequest = createValidPostUserRequest("another-username");
        userService.saveUser(validPostUserRequest);
        addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeWithProvidedIdDoesNotExist_receiveNotFoundStatus() throws IOException {
        // given
        PostUserRequest validPostUserRequest = createValidPostUserRequest("another-username");
        userService.saveUser(validPostUserRequest);
        addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/123";
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeWithProvidedIdDoesNotExist_receiveApiError() throws IOException {
        // given
        PostUserRequest validPostUserRequest = createValidPostUserRequest("another-username");
        userService.saveUser(validPostUserRequest);
        addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/123";
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getMessage()).isEqualTo("Nie znaleziono ogłoszenia");
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeTitleIsNull_receiveBadRequestStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setTitle(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeTitleIsNull_receiveApiErrorMessage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setTitle(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("title")).isEqualTo("To pole nie może być puste");
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeTitleIsTooShort_receiveBadRequest() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setTitle("6chars");
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeTitleIsTooLong_receiveBadRequest() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setTitle("more than 60 characters" + generateLongString(38));
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeTitleLengthIsIncorrect_receiveApiErrorMessage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setTitle("more than 60 characters" + generateLongString(38));
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("title"))
                .isEqualTo("Musi mieć conajmniej 8 i conajwyżej 60 znaków");
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeDescriptionIsNull_receiveBadRequestStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setDescription(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeDescriptionIsNull_receiveApiErrorMessage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setDescription(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("description"))
                .isEqualTo("To pole nie może być puste");
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeDescriptionIsTooShort_receiveBadRequestStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setDescription("less than 60 chars" + generateLongString(40));
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeDescriptionLengthIsIncorrect_receiveApiErrorMessage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setDescription("less than 60 chars" + generateLongString(40));
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("description"))
                .isEqualTo("Musi mieć conajmniej 60 i conajwyżej 2000 znaków");
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeDescriptionIsLongerThan255Chars_doesNotThrowJdbcSQLDataException() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setDescription("more than 255 characters" + generateLongString(240));
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isNotEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeDescriptionIsTooLong_receiveBadRequestStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setDescription("more than 2000 characters" + generateLongString(1990));
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeLocationIsNull_receiveBadRequestStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setLocation(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeLocationIsNull_receiveApiErrorMessage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setLocation(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("location"))
                .isEqualTo("To pole nie może być puste");
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeLocationIsToShort_receiveBadRequestStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setLocation("1c");
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeLocationIsToLong_receiveBadRequestStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setLocation("more than 64 characters" + generateLongString(42));
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticeLocationIsToLong_receiveApiErrorMessage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setLocation("more than 64 characters" + generateLongString(42));
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("location"))
                .isEqualTo("Musi mieć conajmniej 3 i conajwyżej 60 znaków");
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticePriceIsNull_receiveBadRequestStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setPrice(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticePriceIsNull_receiveApiErrorMessage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setPrice(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("price"))
                .isEqualTo("To pole nie może być puste");
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticePriceLengthIsTooLong_receiveBadRequestStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setPrice("10000000.00");
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndNoticePriceLengthIsTooLong_receiveApiErrorMessage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setPrice("10000000.00");
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, ApiError.class);
        // then
        assertThat(response.getBody().getValidationErrors().get("price"))
                .isEqualTo("Musi mieć conajmniej 1 i conajwyżej 10 znaków");
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndPrimaryImageIsNull_receiveOkStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setPrimaryImage(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndPrimaryImageIsNull_doNotChangePrimaryImage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        String oldImageName = noticeRepository.findById(savedNoticeId).get().getPrimaryImage();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setPrimaryImage(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        Optional<Notice> updatedNotice = noticeRepository.findById(savedNoticeId);
        assertThat(updatedNotice.get().getPrimaryImage()).isEqualTo(oldImageName);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndPrimaryImageIsNotNull_changePrimaryImage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        String oldImageName = noticeRepository.findById(savedNoticeId).get().getPrimaryImage();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        String imageAsBase64 = getImageAsBase64();
        validPutUserRequest.setPrimaryImage(imageAsBase64);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        Optional<Notice> updatedNotice = noticeRepository.findById(savedNoticeId);
        assertThat(updatedNotice.get().getPrimaryImage()).isNotEqualTo(oldImageName);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndSecondaryImageIsNull_receiveOkStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setSecondaryImage(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndSecondaryImageIsNull_doNotChangeSecondaryImage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        String oldImageName = noticeRepository.findById(savedNoticeId).get().getSecondaryImage();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setSecondaryImage(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        Optional<Notice> updatedNotice = noticeRepository.findById(savedNoticeId);
        assertThat(updatedNotice.get().getSecondaryImage()).isEqualTo(oldImageName);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndSecondaryImageIsNotNull_changeSecondaryImage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        String oldImageName = noticeRepository.findById(savedNoticeId).get().getSecondaryImage();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        String imageAsBase64 = getImageAsBase64();
        validPutUserRequest.setSecondaryImage(imageAsBase64);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        Optional<Notice> updatedNotice = noticeRepository.findById(savedNoticeId);
        assertThat(updatedNotice.get().getSecondaryImage()).isNotEqualTo(oldImageName);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndTertiaryImageIsNull_receiveOkStatus() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setTertiaryImage(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndTertiaryImageIsNull_doNotChangeTertiaryImage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        String oldImageName = noticeRepository.findById(savedNoticeId).get().getTertiaryImage();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        validPutUserRequest.setTertiaryImage(null);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        Optional<Notice> updatedNotice = noticeRepository.findById(savedNoticeId);
        assertThat(updatedNotice.get().getTertiaryImage()).isEqualTo(oldImageName);
    }

    @Test
    public void putNotice_whenUserIsAuthorizedAndTertiaryImageIsNotNull_changeTertiaryImage() throws IOException {
        // given
        Long savedNoticeId = setupNoticeForUpdate();
        String oldImageName = noticeRepository.findById(savedNoticeId).get().getTertiaryImage();
        // when
        PutNoticeRequest validPutUserRequest = createValidPutUserRequest();
        String imageAsBase64 = getImageAsBase64();
        validPutUserRequest.setTertiaryImage(imageAsBase64);
        HttpEntity<PutNoticeRequest> requestHttpEntity = new HttpEntity<>(validPutUserRequest);
        String url = NOTICES_URL + "/" + savedNoticeId;
        testRestTemplate.exchange(url, PUT, requestHttpEntity, Object.class);
        // then
        Optional<Notice> updatedNotice = noticeRepository.findById(savedNoticeId);
        assertThat(updatedNotice.get().getTertiaryImage()).isNotEqualTo(oldImageName);
    }

    private Long setupNoticeForUpdate() throws IOException {
        PostUserRequest validPostUserRequest = createValidPostUserRequest("test-username");
        Long creatorId = userService.saveUser(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        return noticeService.postNotice(postNoticeRequest, creator);
    }
}
