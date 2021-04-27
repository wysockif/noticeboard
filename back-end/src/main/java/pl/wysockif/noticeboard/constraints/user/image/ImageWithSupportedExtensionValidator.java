package pl.wysockif.noticeboard.constraints.user.image;

import org.apache.tika.Tika;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;

public class ImageWithSupportedExtensionValidator implements ConstraintValidator<ImageWithSupportedExtension, String> {
    @Override
    public boolean isValid(String imageAsEncodedString, ConstraintValidatorContext constraintValidatorContext) {
        if (imageAsEncodedString != null) {
            byte[] decodedImage = Base64.getDecoder().decode(imageAsEncodedString);
            Tika tika = new Tika();
            String detected = tika.detect(decodedImage);
            return detected.equalsIgnoreCase("image/png")
                    || detected.equalsIgnoreCase("image/jpeg")
                    || detected.equalsIgnoreCase("image/jpg");
        } else {
            return true;
        }
    }
}
