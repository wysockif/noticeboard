package pl.wysockif.noticeboard.config.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// https://stackoverflow.com/questions/31424196/disable-browser-authentication-dialog-in-spring-security/50023070
public class NoPopupBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String message = request.getServletPath().equals("/api/1.0/login") ? "Niepoprawne dane logowania" : "Brak autoryzacji";
        response.sendError(HttpStatus.UNAUTHORIZED.value(), message);
    }
}
