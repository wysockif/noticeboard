package pl.wysockif.noticeboard.config.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

// https://stackoverflow.com/questions/31424196/disable-browser-authentication-dialog-in-spring-security/50023070
public class NoPopupBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        if (authException.getMessage().equals("User account is locked")) {
            String message = "Adres email nie został potwierdzony";
            response.sendError(FORBIDDEN.value(), message);
        } else if( request.getServletPath().equals("/api/1.0/login")){
            String message = "Niepoprawne dane logowania";
            response.sendError(UNAUTHORIZED.value(), message);
        } else {
            String message = "Brak autoryzacji";
            response.sendError(UNAUTHORIZED.value(), message);
        }

    }
}
