package pl.wysockif.noticeboard.config.web;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

import static java.util.concurrent.TimeUnit.DAYS;

@Configuration
@Getter
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload-folder-path}")
    private String uploadFolderPath;

    @Bean
    public CommandLineRunner createFolderForUploads() {
        return args -> {
            checkIfFolderExistsAndCreateItIfNot(uploadFolderPath);
            checkIfFolderExistsAndCreateItIfNot(uploadFolderPath + "/profile-images");
            checkIfFolderExistsAndCreateItIfNot(uploadFolderPath + "/notice-images");
        };
    }

    private void checkIfFolderExistsAndCreateItIfNot(String folderPath) {
        File folder = new File(folderPath);
        boolean isFolderCorrect = folder.isDirectory() && folder.exists();
        if (!isFolderCorrect) {
            folder.mkdir();
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        resourceHandlerRegistry.addResourceHandler("images/profile/**")
                .addResourceLocations("file:" + uploadFolderPath + "/profile-images/")
                .setCacheControl(CacheControl.maxAge(90, DAYS));
        resourceHandlerRegistry.addResourceHandler("images/notice/**")
                .addResourceLocations("file:" + uploadFolderPath + "/notice-images/")
                .setCacheControl(CacheControl.maxAge(30, DAYS));
    }

}
