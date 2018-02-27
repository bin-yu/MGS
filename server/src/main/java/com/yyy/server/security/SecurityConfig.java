package com.yyy.server.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Profile("!test")
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, MgsAuthenticationProvider provider) throws Exception {
        auth.authenticationProvider(provider);
    }

    @Profile("!test")
    @Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public static class APIConfiguration extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/**").authorizeRequests().anyRequest().authenticated();
            http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
                @Override
                public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
                }

            });
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
            CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
            csrfTokenRepository.setCookieHttpOnly(false);//allow angular to read it
            http.csrf().csrfTokenRepository(csrfTokenRepository);
            http.logout().logoutUrl("/api/logout").logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
        }
    }
    @Profile("!test")
    @Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public static class FrontendConfiguration extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/**").authorizeRequests().antMatchers("/css/**", "/assets/**", "/*.*").permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login")
                                            .permitAll();
            http.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
            //http.logout().logoutUrl("/api/logout").logoutSuccessUrl("/login");
            CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
            csrfTokenRepository.setCookieHttpOnly(false);//allow angular to read it
            http.csrf().csrfTokenRepository(csrfTokenRepository);
        }
    }






}
