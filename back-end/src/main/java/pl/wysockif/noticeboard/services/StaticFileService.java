package pl.wysockif.noticeboard.services;

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


    public String saveProfileImage(String id, String imageAsEncodedString) {
        LOGGER.info("Saving profile image (userId: " + id + ")");
        String profileImageFileName = id + "-" +
                UUID.randomUUID().toString().replace("-", "") + ".png";

        byte[] decodedImage = Base64.getDecoder().decode(imageAsEncodedString);
        File file = new File(uploadFolderPath + "/profile-images/" + profileImageFileName);
        tryToCreateFile(decodedImage, file);
        LOGGER.info("Saved profile image (userId: " + id + ")");
        return profileImageFileName;
    }

    private void tryToCreateFile(byte[] decodedImage, File file) throws FileIOException {
        try {
            FileUtils.writeByteArrayToFile(file, decodedImage);
        } catch (IOException e) {
            LOGGER.warning("Cannot create user image (name: " + file.getName() + ")");
            throw new FileIOException();
        }
    }

    public void deleteOldProfileImage(String image) {
        if (image != null) {
            try {
                Files.deleteIfExists(Paths.get(uploadFolderPath + "/profile-images/" + image));
            } catch (IOException e) {
                LOGGER.warning("Cannot delete old user image (name: " + image + ")");
            }
        }
    }
}
