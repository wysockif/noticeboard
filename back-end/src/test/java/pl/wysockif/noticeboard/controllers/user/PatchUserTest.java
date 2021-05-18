package pl.wysockif.noticeboard.controllers.user;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pl.wysockif.noticeboard.TestUtils;
import pl.wysockif.noticeboard.dto.user.requests.PatchUserRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.repositories.user.AppUserRepository;
import pl.wysockif.noticeboard.services.user.AppUserService;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PatchUserTest {

    private static final String USERS_URL = "/api/1.0/users";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AppUserService userService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Before
    public void setUp() {
        noticeRepository.deleteAll();
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void patchUser_whenUserIsUnauthorized_receiveUnauthorizedStatus() {
        // given
        String id = "10";
        // when
        String url = USERS_URL + "/" + id;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, null, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void patchUser_whenAuthorizedUserChangesFirstNameAndLastName_receiveOkStatus() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        String id = String.valueOf(currentUserId);
        PatchUserRequest patchUserRequest = new PatchUserRequest("UpdatedFirstName", "UpdatedLastName");
        HttpEntity<PatchUserRequest> requestHttpEntity = new HttpEntity<>(patchUserRequest);
        // when
        String url = USERS_URL + "/" + id;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void patchUser_whenAuthorizedUserChangesFirstNameAndLastName_fieldsUpdated() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        PatchUserRequest patchUserRequest = new PatchUserRequest("UpdatedFirstName", "UpdatedLastName");
        HttpEntity<PatchUserRequest> requestHttpEntity = new HttpEntity<>(patchUserRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id;
        testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        Optional<AppUser> updatedUser = userRepository.findById(currentUserId);
        assertThat(updatedUser.get().getFirstName()).isEqualTo(patchUserRequest.getFirstName());
        assertThat(updatedUser.get().getLastName()).isEqualTo(patchUserRequest.getLastName());
    }

    @Test
    public void patchUser_whenUserIsUpdatingAnotherUser_receiveForbiddenStatus() {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        PatchUserRequest patchUserRequest = new PatchUserRequest("UpdatedFirstName", "UpdatedLastName");
        HttpEntity<PatchUserRequest> requestHttpEntity = new HttpEntity<>(patchUserRequest);
        String anotherUserId = String.valueOf(currentUserId) + '1';
        // when
        String url = USERS_URL + "/" + anotherUserId;
        ResponseEntity<Object> response = testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    public void patchUser_whenAuthorizedUserChangesImage_fieldUpdated() throws IOException {
        // given
        PostUserRequest user = TestUtils.createValidPostUserRequest("username1");
        Long currentUserId = userService.saveUser(user);
        TestUtils.addAuthenticationInterceptor(testRestTemplate, user);
        PatchUserRequest patchUserRequest = new PatchUserRequest("UpdatedFirstName", "UpdatedLastName");
        ClassPathResource profileImage = new ClassPathResource("default-profile-image.jpeg");
        byte[] imageAsByteArray = FileUtils.readFileToByteArray(profileImage.getFile());
        String imageAsEncodedString = Base64.getEncoder().encodeToString(imageAsByteArray);
        patchUserRequest.setProfileImage(imageAsEncodedString);
        HttpEntity<PatchUserRequest> requestHttpEntity = new HttpEntity<>(patchUserRequest);
        String id = String.valueOf(currentUserId);
        // when
        String url = USERS_URL + "/" + id;
        testRestTemplate.exchange(url, PATCH, requestHttpEntity, Object.class);
        // then
        Optional<AppUser> updatedUser = userRepository.findById(currentUserId);
        assertThat(updatedUser.get().getImage()).isNotNull();
    }

}
