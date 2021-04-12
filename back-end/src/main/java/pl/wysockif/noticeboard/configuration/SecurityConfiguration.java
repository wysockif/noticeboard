package pl.wysockif.noticeboard.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthUserService authUserService;

    public SecurityConfiguration(AuthUserService authUserService) {
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

    // https://stackoverflow.com/questions/31424196/disable-browser-authentication-dialog-in-spring-security/50023070
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().authenticationEntryPoint(new NoPopupBasicAuthenticationEntryPoint());

        http.authorizeRequests().antMatchers(POST, "/api/1.0/login").authenticated()
                .and()
                .authorizeRequests().anyRequest().permitAll();

        http.sessionManagement().sessionCreationPolicy(STATELESS);

        http.csrf().disable();
    }
}
