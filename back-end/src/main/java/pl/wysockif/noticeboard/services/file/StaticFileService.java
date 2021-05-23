package pl.wysockif.noticeboard.services.file;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.errors.FileIOException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class StaticFileService {

    @Value("${upload-folder-path}")
    private String uploadFolderPath;

    private final Logger LOGGER = Logger.getLogger(StaticFileService.class.getName());


    public String saveNoticeImage(String noticeId, String imageAsEncodedString) {
        String noticeImageFileName = noticeId + "-" +
                UUID.randomUUID().toString().replace("-", "");
        LOGGER.info("Saving notice image (noticeId: " + noticeId + ", imageName: " + noticeImageFileName + ")");

        byte[] decodedImage = Base64.getDecoder().decode(imageAsEncodedString);
        File file = new File(uploadFolderPath + "/notice-images/" + noticeImageFileName);
        tryToCreateFile(decodedImage, file, noticeId);
        LOGGER.info("Saved notice image (noticeId: " + noticeId + ", imageName: " + file.getName() + ")");
        return noticeImageFileName;
    }

    public String saveProfileImage(String userId, String username, String imageAsEncodedString) {
        String profileImageFileName = username + "-" +
                UUID.randomUUID().toString().replace("-", "");
        LOGGER.info("Saving profile image (userId: " + userId + ", imageName: " + profileImageFileName + ")");

        byte[] decodedImage = Base64.getDecoder().decode(imageAsEncodedString);
        File file = new File(uploadFolderPath + "/profile-images/" + profileImageFileName);
        tryToCreateFile(decodedImage, file, userId);
        LOGGER.info("Saved profile image (userId: " + userId + ", imageName: " + file.getName() + ")");
        return profileImageFileName;
    }

    private void tryToCreateFile(byte[] decodedImage, File file, String userId) throws FileIOException {
        try {
            FileUtils.writeByteArrayToFile(file, decodedImage);
        } catch (IOException e) {
            LOGGER.warning("Cannot create user image (userId: " + userId + ", imageName: " + file.getName() + ")");
            throw new FileIOException();
        }
    }

    public void deleteProfileImage(String userId, String image) {
        if (image != null) {
            LOGGER.info("Deleting user image (userId: " + userId + ", imageName: " + image + ")");
            try {
                Files.deleteIfExists(Paths.get(uploadFolderPath + "/profile-images/" + image));
                LOGGER.info("Deleted user image (userId: " + userId + ", imageName: " + image + ")");
            } catch (IOException e) {
                LOGGER.warning("Cannot delete user image (userId: " + userId + ", imageName: " + image + ")");
            }
        }
    }

    public void deleteNoticeImage(String noticeId, String image) {
        if (image != null) {
            LOGGER.info("Deleting notice image (noticeId: " + noticeId + ", imageName: " + image + ")");
            try {
                Files.deleteIfExists(Paths.get(uploadFolderPath + "/notice-images/" + image));
                LOGGER.info("Deleted notice image (noticeId: " + noticeId + ", imageName: " + image + ")");
            } catch (IOException e) {
                LOGGER.warning("Cannot delete notice image (noticeId: " + noticeId + ", imageName: " + image + ")");
            }
        }
    }
}
