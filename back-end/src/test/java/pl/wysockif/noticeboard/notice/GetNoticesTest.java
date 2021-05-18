package pl.wysockif.noticeboard.notice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import pl.wysockif.noticeboard.TestPage;
import pl.wysockif.noticeboard.TestUtils;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.notice.Notice;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.notice.NoticeService;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class GetNoticesTest {
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
    public void getNotices_whenThereAreNoNoticesInDatabase_receiveOkStatus() {
        // given
        // when
        ResponseEntity<Object> response = testRestTemplate.getForEntity(NOTICES_URL, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
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
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
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
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
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
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
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
    public void getNotices_whenThereIs15NoticesInDatabaseAndRequestedSizeIsNotGiven_receivePageWith12Notices() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.save(validPostUserRequest);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        saveNValidNotices(creator, 20);
        // when
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(NOTICES_URL, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(12);
    }

    @Test
    @Transactional
    public void getNotices_whenUserUsernameIsProvidedInRequestBody_receiveNoticesOfThatUser() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.save(firstValidPostUserRequest);
        Long firstCreatorId = userService.save(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNotices(firstCreator, 5);
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        PostUserRequest secondValidPostUserRequest = TestUtils.createValidPostUserRequest("second-username");
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
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
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

    @Test
    public void getNoticeById_whenThereIsNoticeWithProvidedId_receiveOkStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        Long savedNoticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
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

    private void saveNValidNotices(AppUser creator, int n) throws IOException {
        for (int i = 0; i < n; i++) {
            PostNoticeRequest validNotice = TestUtils.createValidPostNoticeRequest();
            noticeService.postNotice(validNotice, creator);
        }
    }

}
