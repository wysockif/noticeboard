package pl.wysockif.noticeboard.config.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class NoPopupBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        if (authException.getMessage().equals("User account is locked")) {
            String message = "To konto nie zostało aktywowane";
            response.sendError(FORBIDDEN.value(), message);
        } else if (request.getServletPath().equals("/api/1.0/login")) {
            String message = "Niepoprawne dane logowania";
            response.sendError(UNAUTHORIZED.value(), message);
        } else {
            String message = "Brak autoryzacji";
            response.sendError(UNAUTHORIZED.value(), message);
        }
    }

}
