package pl.wysockif.noticeboard.user;

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
import pl.wysockif.noticeboard.dto.user.snapshots.AppUserSnapshot;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.notice.NoticeService;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class GetUserTest {
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

    @Before
    public void setUp() {
        noticeRepository.deleteAll();
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void getUserByNoticeId_whenNoticeExist_receiveOkStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        Long noticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
        // when
        String url = USERS_URL + "/notice/" + noticeId;
        ResponseEntity<Object> response = testRestTemplate.getForEntity(url, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void getUserByNoticeId_whenNoticeExist_receiveCreatorSnapshot() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        Long creatorId = userService.save(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        Long noticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
        // when
        String url = USERS_URL + "/notice/" + noticeId;
        ResponseEntity<AppUserSnapshot> response = testRestTemplate.getForEntity(url, AppUserSnapshot.class);
        // then
        assertThat(response.getBody().getUsername()).isEqualTo(username);
    }

    @Test
    public void getUserByNoticeId_whenNoticeDoesNotExist_receiveNotFoundStatus() throws IOException {
        // given
        long nonExistingNoticeId = 123L;
        // when
        String url = USERS_URL + "/notice/" + nonExistingNoticeId;
        ResponseEntity<Object> response = testRestTemplate.getForEntity(url, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void getUserByUsername_whenUserExists_receiveOk() {
        // given
        String username = "username1";
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.save(postUserRequest);
        // when
        String url = USERS_URL + "/" + username;
        ResponseEntity<Object> response = testRestTemplate.getForEntity(url, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void getUserByUsername_whenUserExists_receiveUserDataWithoutPassword() {
        // given
        String username = "username1";
        PostUserRequest postUserRequest = TestUtils.createValidPostUserRequest(username);
        userService.save(postUserRequest);
        // when
        String url = USERS_URL + "/" + username;
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        // then
        assertThat(response.getBody().contains("password")).isFalse();
    }

    @Test
    public void getUserByUsername_whenUserDoesNotExist_receiveNotFoundStatus() {
        // given
        String username = "username1";
        // when
        String url = USERS_URL + "/" + username;
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }


}
