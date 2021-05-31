package pl.wysockif.noticeboard.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationUserService authUserService;
    public static final String API_URL = "/api/1.0";

    public SecurityConfig(AuthenticationUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authUserService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().authenticationEntryPoint(new NoPopupBasicAuthenticationEntryPoint());

        http.authorizeRequests()
                .antMatchers(POST, API_URL + "/login").authenticated()
                .antMatchers(PATCH, API_URL + "/users/{id:[0-9]+}").authenticated()
                .antMatchers(DELETE, API_URL + "/users/{id:[0-9]+}").authenticated()
                .antMatchers(POST, API_URL + "/notices").authenticated()
                .antMatchers(DELETE, API_URL + "/notices/{id:[0-9]+}").authenticated()
                .antMatchers(PUT, API_URL + "/notices/{id:[0-9]+}").authenticated()
                .and()
                .authorizeRequests().anyRequest().permitAll();

        http.sessionManagement().sessionCreationPolicy(STATELESS);

        http.cors();

        http.csrf().disable();
    }
}



