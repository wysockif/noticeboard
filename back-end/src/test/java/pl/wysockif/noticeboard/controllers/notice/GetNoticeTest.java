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
import pl.wysockif.noticeboard.TestUtils;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
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
public class GetNoticeTest {
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
    public void getNotice_whenThereIsNoticeWithProvidedId_receiveOkStatus() throws IOException {
        // given
        String username = "test-username";
        PostUserRequest validPostUserRequest = TestUtils.createValidPostUserRequest(username);
        Long creatorId = userService.saveUser(validPostUserRequest);
        AppUser creator = userRepository.getOne(creatorId);
        Long savedNoticeId = noticeService.postNotice(TestUtils.createValidPostNoticeRequest(), creator);
        // when
        String url = NOTICES_URL + '/' + savedNoticeId;
        ResponseEntity<Object> response = testRestTemplate.getForEntity(url, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void getNotice_whenThereIsNotNoticeWithProvidedId_receiveNotFoundStatus() {
        // given
        String nonExistingNoticeId = "123";
        // when
        String url = NOTICES_URL + '/' + nonExistingNoticeId;
        ResponseEntity<Object> response = testRestTemplate.getForEntity(url, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }
}
