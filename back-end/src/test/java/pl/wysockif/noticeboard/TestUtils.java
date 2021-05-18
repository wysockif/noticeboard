package pl.wysockif.noticeboard;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.dto.notice.requests.PutNoticeRequest;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;

import java.io.IOException;
import java.util.Base64;

public class TestUtils {

    public static String generateLongString(int length) {
        return new String(new char[length]).replace('\0', 'u');
    }

    public static String getImageAsBase64() throws IOException {
        ClassPathResource imageResource = new ClassPathResource("default-notice-image.jpg");
        byte[] imageArr = FileUtils.readFileToByteArray(imageResource.getFile());
        return Base64.getEncoder().encodeToString(imageArr);
    }

    public static PostNoticeRequest createValidPostNoticeRequest() throws IOException {
        String imageAsBase64 = getImageAsBase64();
        String noticeDescription = "Notice description " + generateLongString(60);
        PostNoticeRequest postNoticeRequest = new PostNoticeRequest();
        postNoticeRequest.setTitle("Notice title");
        postNoticeRequest.setDescription(noticeDescription);
        postNoticeRequest.setPrice("12.23");
        postNoticeRequest.setLocation("Notice Location");
        postNoticeRequest.setPrimaryImage(imageAsBase64);
        postNoticeRequest.setSecondaryImage(imageAsBase64);
        postNoticeRequest.setTertiaryImage(imageAsBase64);
        return postNoticeRequest;
    }

    public static PostUserRequest createValidPostUserRequest(String username) {
        PostUserRequest postUserRequest = new PostUserRequest();
        postUserRequest.setUsername(username);
        postUserRequest.setEmail("usermail@email.com");
        postUserRequest.setFirstName("Firstname");
        postUserRequest.setLastName("Lastname");
        postUserRequest.setPassword("Password123");
        return postUserRequest;
    }

    public static void addAuthenticationInterceptor(TestRestTemplate testRestTemplate, PostUserRequest user) {
        testRestTemplate.getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor(user.getUsername(), user.getPassword()));
    }

    public static PutNoticeRequest createValidPutUserRequest() throws IOException {
        String imageAsBase64 = getImageAsBase64();
        String noticeDescription = "Updated notice description " + generateLongString(60);
        PutNoticeRequest putNoticeRequest = new PutNoticeRequest();
        putNoticeRequest.setTitle("Updated title");
        putNoticeRequest.setDescription(noticeDescription);
        putNoticeRequest.setPrice("12.00");
        putNoticeRequest.setLocation("Updated Location");
        putNoticeRequest.setPrimaryImage(imageAsBase64);
        putNoticeRequest.setSecondaryImage(imageAsBase64);
        putNoticeRequest.setTertiaryImage(imageAsBase64);
        return putNoticeRequest;
    }
}
