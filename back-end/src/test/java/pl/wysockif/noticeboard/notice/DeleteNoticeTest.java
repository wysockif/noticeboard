package pl.wysockif.noticeboard.notice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.TestUtils;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.ApiError;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.notice.NoticeService;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class DeleteNoticeTest {
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
    public void deleteNoticeById_whenUserIsAuthorizedAndThereIsNoticeWithProvidedId_receiveOkStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        Long savedNoticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
        // when
        String url = NOTICES_URL + '/' + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, DELETE, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void deleteNoticeById_whenUserIsAuthorizedAndThereIsNoticeWithProvidedId_deletedNoticeFromDatabase() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        Long savedNoticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
        // when
        String url = NOTICES_URL + '/' + savedNoticeId;
        testRestTemplate.exchange(url, DELETE, null, Object.class);
        // then
        assertThat(noticeRepository.count()).isEqualTo(0);
    }

    @Test
    public void deleteNoticeById_whenUserIsUnauthorizedAndThereIsNoticeWithProvidedId_receiveUnauthorizedStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        Long savedNoticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
        // when
        String url = NOTICES_URL + '/' + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, DELETE, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void deleteNoticeById_whenUserIsUnauthorizedAndThereIsNoticeWithProvidedId__receiveApiErrorMessage() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        Long savedNoticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
        // when
        String url = NOTICES_URL + '/' + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, DELETE, null, ApiError.class);
        // then
        assertThat(response.getBody().getMessage()).isEqualTo("Brak autoryzacji");
    }

    @Test
    public void deleteNoticeById_whenUserIsAuthorizedAndNoticeWithProvidedIdDoesNotExist_receiveNotFoundStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        userService.save(validPostUserRequest);
        long anyNonExistingNoticeId = 1234L;
        // when
        String url = NOTICES_URL + '/' + anyNonExistingNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, DELETE, null, ApiError.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void deleteNoticeById_whenAuthorizedUserDeletesAnotherUserNoticeWithProvidedId_receiveForbidden() throws IOException {
        // given
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest("test-username-1");
        TestUtils.addAuthenticationInterceptor(testRestTemplate, validPostUserRequest);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        Long savedNoticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        PostUserRequest anotherPostUserRequest = TestUtils.createValidPostUserRequest("test-username-2");
        TestUtils.addAuthenticationInterceptor(testRestTemplate, anotherPostUserRequest);
        userService.save(anotherPostUserRequest);
        // when
        String url = NOTICES_URL + '/' + savedNoticeId;
        ResponseEntity<ApiError> response = testRestTemplate.exchange(url, DELETE, null, ApiError.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

}
