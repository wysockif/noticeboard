package pl.wysockif.noticeboard.constraints.user.image;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ImageWithSupportedExtensionValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageWithSupportedExtension {

    String message() default "{noticeboard.constraints.ImageWithSupportedExtension.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
