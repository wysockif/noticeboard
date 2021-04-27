package pl.wysockif.noticeboard.services;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.errors.FileIOException;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class StaticFileService {

    @Value("${upload-folder-path}")
    private String uploadFolderPath;

    public String saveProfileImage(String username, String imageAsEncodedString) {
        String profileImageFileName = username + "-" +
                UUID.randomUUID().toString().replace("-", "") + ".png";

        byte[] decodedImage = Base64.getDecoder().decode(imageAsEncodedString);
        File file = new File(uploadFolderPath + "/profile-images/" + profileImageFileName);
        tryToCreateFile(decodedImage, file);
        return profileImageFileName;
    }

    private void tryToCreateFile(byte[] decodedImage, File file) throws FileIOException {
        try {
            FileUtils.writeByteArrayToFile(file, decodedImage);
        } catch (IOException e) {
            throw new FileIOException();
        }
    }
}
