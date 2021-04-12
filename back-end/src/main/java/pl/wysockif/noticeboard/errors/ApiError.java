package pl.wysockif.noticeboard.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_NULL)  // https://stackoverflow.com/questions/11757487/how-to-tell-jackson-to-ignore-a-field-during-serialization-if-its-value-is-null
public class ApiError {

    private String message;

    private Map<String, String> validationErrors;

    public ApiError(String message) {
        this.message = message;
    }

}

