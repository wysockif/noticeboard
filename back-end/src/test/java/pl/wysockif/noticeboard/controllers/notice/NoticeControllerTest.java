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
        postNoticeRequest.setTitle("more Than 30 characters " + generateLongString(30));
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
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
    public void postNotice_whenUserIsAuthorizedAndNoticeDescriptionIsLongerThan255Chars_doesNotThrowJdbcSQLDataException() {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        addAuthenticationInterceptor(validPostUserRequest);
        PostNoticeRequest postNoticeRequest = createValidPostNoticeRequest();
        postNoticeRequest.setDescription("more than 255 characters" + generateLongString(240));
        System.out.println(postNoticeRequest.getDescription());
        // when
        ResponseEntity<Object> response = testRestTemplate.postForEntity(NOTICES_URL, postNoticeRequest, Object.class);
        // then
        assertThat(response.getStatusCode()).isNotEqualTo(INTERNAL_SERVER_ERROR);
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

    private PostNoticeRequest createValidPostNoticeRequest() {
        PostNoticeRequest postNoticeRequest = new PostNoticeRequest();
        postNoticeRequest.setTitle("Notice title");
        postNoticeRequest.setDescription("Notice description " + generateLongString(60));
        postNoticeRequest.setPrice("12.23");
        postNoticeRequest.setLocation("Notice Location");
        postNoticeRequest.setPrimaryImage("NoticePrimaryImage.png");
        postNoticeRequest.setSecondaryImage("NoticeSecondaryImage.png");
        postNoticeRequest.setTertiaryImage("NoticeTertiaryImage.png");
        postNoticeRequest.setKeywords(List.of("NoticeKeyword1", "NoticeKeyword2"));
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