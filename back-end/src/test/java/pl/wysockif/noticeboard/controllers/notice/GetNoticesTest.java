package pl.wysockif.noticeboard.controllers.notice;

import org.junit.Before;
import org.junit.Rule;
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
import pl.wysockif.noticeboard.SmtpServerRule;
import pl.wysockif.noticeboard.TestPage;
import pl.wysockif.noticeboard.TestUtils;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.notice.Notice;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.token.VerificationTokenRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.notice.NoticeService;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.GET;
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
        Long creatorId = userService.saveUser(validPostUserRequest);
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
        userService.saveUser(validPostUserRequest);
        Long creatorId = userService.saveUser(validPostUserRequest);
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
        userService.saveUser(validPostUserRequest);
        Long creatorId = userService.saveUser(validPostUserRequest);
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
        userService.saveUser(validPostUserRequest);
        Long creatorId = userService.saveUser(validPostUserRequest);
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
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNotices(firstCreator, 5);
        testRestTemplate.getRestTemplate().getInterceptors().clear();
        PostUserRequest secondValidPostUserRequest = TestUtils.createValidPostUserRequest("second-username");
        userService.saveUser(secondValidPostUserRequest);
        Long secondCreatorId = userService.saveUser(secondValidPostUserRequest);
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
    public void getNotices_whenUserUsernameIsProvidedInUrlButThisUserDoesNotExist_receivePageWithZeroNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
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
    public void getNotices_whenLocationIsProvidedInUrlAndThereAreThreeNoticesInThisLocationAndEightInTotal_receivePageWithThreeNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNoticesWithSpecifiedLocation(firstCreator, "Location1", 5);
        saveNValidNoticesWithSpecifiedLocation(firstCreator, "Location2", 3);
        // when
        String url = NOTICES_URL + "?location=Location2";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(3);
    }

    @Test
    public void getNotices_whenLocationIsProvidedInUrlButThereIsNotAnyNoticeWithThisLocation_receivePageWithZeroNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNoticesWithSpecifiedLocation(firstCreator, "Location1", 5);
        // when
        String url = NOTICES_URL + "?location=Location2";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(0);
    }

    @Test
    public void getNotices_whenMinPriceIsProvidedInUrlAndThereAreThreeNoticesWithMinimallyThisPriceAndEightInTotal_receivePageWithThreeNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNoticesWithSpecifiedPrice(firstCreator, "100", 5);
        saveNValidNoticesWithSpecifiedPrice(firstCreator, "200", 3);
        // when
        String url = NOTICES_URL + "?minPrice=150";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(3);
    }

    @Test
    public void getNotices_whenMinPriceIsProvidedInUrlButThereIsNotAnyNoticeWithMinimallyThisPrice_receivePageWithZeroNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNoticesWithSpecifiedPrice(firstCreator, "100", 5);
        // when
        String url = NOTICES_URL + "?minPrice=150";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(0);
    }

    @Test
    public void getNotices_whenMaxPriceIsProvidedInUrlAndThereAreThreeNoticesWithMaximallyThisPriceAndEightInTotal_receivePageWithThreeNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNoticesWithSpecifiedPrice(firstCreator, "200", 5);
        saveNValidNoticesWithSpecifiedPrice(firstCreator, "100", 3);
        // when
        String url = NOTICES_URL + "?maxPrice=150";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(3);
    }

    @Test
    public void getNotices_whenMaxPriceIsProvidedInUrlButThereIsNotAnyNoticeWithMaximallyThisPrice_receivePageWithZeroNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNoticesWithSpecifiedPrice(firstCreator, "200", 5);
        // when
        String url = NOTICES_URL + "?maxPrice=150";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(0);
    }

    @Test
    public void getNotices_whenSearchQueryIsProvidedInUrlAndThereAreThreeNoticesWithTitleContainingThisQueryAndEightInTotal_receivePageWithThreeNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNoticesWithSpecifiedTitle(firstCreator, "Lorem ipsum", 5);
        saveNValidNoticesWithSpecifiedTitle(firstCreator, "dolor sit amet", 3);
        // when
        String url = NOTICES_URL + "?searched=sit+amet";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(3);
    }


    @Test
    public void getNotices_whenSearchQueryIsProvidedInUrlButThereIsNotAnyNoticeWithTitleContainingThisQuery_receivePageWithZeroNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNoticesWithSpecifiedTitle(firstCreator, "Lorem ipsum", 5);
        saveNValidNoticesWithSpecifiedTitle(firstCreator, "dolor sit amet", 3);
        // when
        String url = NOTICES_URL + "?searched=consectetur+adipiscing+elit";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(0);
    }


    @Test
    public void getNotices_whenSearchQueryIsProvidedInUrlAndThereAreThreeNoticesWithDescriptionContainingThisQueryAndEightInTotal_receivePageWithThreeNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNoticesWithSpecifiedDescription(firstCreator, "Lorem ipsum", 5);
        saveNValidNoticesWithSpecifiedDescription(firstCreator, "dolor sit amet", 3);
        // when
        String url = NOTICES_URL + "?searched=sit+amet";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(3);
    }


    @Test
    public void getNotices_whenSearchQueryIsProvidedInUrlButThereIsNotAnyNoticeWithDescriptionContainingThisQuery_receivePageWithZeroNotices() throws IOException {
        // given
        PostUserRequest firstValidPostUserRequest = TestUtils.createValidPostUserRequest("first-username");
        userService.saveUser(firstValidPostUserRequest);
        Long firstCreatorId = userService.saveUser(firstValidPostUserRequest);
        AppUser firstCreator = userRepository.getOne(firstCreatorId);
        saveNValidNoticesWithSpecifiedDescription(firstCreator, "Lorem ipsum", 5);
        saveNValidNoticesWithSpecifiedDescription(firstCreator, "dolor sit amet", 3);
        // when
        String url = NOTICES_URL + "?searched=consectetur+adipiscing+elit";
        ResponseEntity<TestPage<Notice>> response = testRestTemplate.exchange(url, GET, null,
                new ParameterizedTypeReference<>() {
                });
        // then
        assertThat(response.getBody().getContent().size()).isEqualTo(0);
    }

    private void saveNValidNotices(AppUser creator, int n) throws IOException {
        for (int i = 0; i < n; i++) {
            PostNoticeRequest validNotice = TestUtils.createValidPostNoticeRequest();
            noticeService.postNotice(validNotice, creator);
        }
    }

    private void saveNValidNoticesWithSpecifiedLocation(AppUser creator, String location, int n) throws IOException {
        for (int i = 0; i < n; i++) {
            PostNoticeRequest validNotice = TestUtils.createValidPostNoticeRequest();
            validNotice.setLocation(location);
            noticeService.postNotice(validNotice, creator);
        }
    }

    private void saveNValidNoticesWithSpecifiedPrice(AppUser creator, String price, int n) throws IOException {
        for (int i = 0; i < n; i++) {
            PostNoticeRequest validNotice = TestUtils.createValidPostNoticeRequest();
            validNotice.setPrice(price);
            noticeService.postNotice(validNotice, creator);
        }
    }

    private void saveNValidNoticesWithSpecifiedTitle(AppUser creator, String title, int n) throws IOException {
        for (int i = 0; i < n; i++) {
            PostNoticeRequest validNotice = TestUtils.createValidPostNoticeRequest();
            validNotice.setTitle(title);
            noticeService.postNotice(validNotice, creator);
        }
    }

    private void saveNValidNoticesWithSpecifiedDescription(AppUser creator, String description, int n) throws IOException {
        for (int i = 0; i < n; i++) {
            PostNoticeRequest validNotice = TestUtils.createValidPostNoticeRequest();
            validNotice.setDescription(description);
            noticeService.postNotice(validNotice, creator);
        }
    }
}
