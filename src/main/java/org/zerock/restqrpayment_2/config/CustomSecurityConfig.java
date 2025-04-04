package org.zerock.restqrpayment_2.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.zerock.restqrpayment_2.security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class CustomSecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain ownerFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/owner/**", "/api/owner/**")
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/api/owner/signup",
                    "/api/owner/account",
                    "/api/owner/password",
                    "/owner/find/id",
                    "/owner/find/password"
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/owner/login", "/owner/signup", "/api/owner/signup", "/owner/find/id", "/owner/find/password", "/css/**", "/js/**", "/images/**", "/webjars/**", "/assets/**").permitAll()
                .requestMatchers("/owner/dashboard", "/owner/dashboard/**").hasAuthority("ROLE_OWNER")
                .requestMatchers("/api/owner/**").hasAuthority("ROLE_OWNER")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/owner/login")
                .loginProcessingUrl("/owner/login")
                .defaultSuccessUrl("/owner/dashboard", true)
                .failureUrl("/owner/login?error=true")
                .usernameParameter("userId")
                .passwordParameter("password")
                .permitAll()
            )
            .userDetailsService(userDetailsService)
            .logout(logout -> logout
                .logoutUrl("/owner/logout")
                .logoutSuccessUrl("/owner/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**")
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/find/id", "/find/password", "/api/orders/**")
                .disable()
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/signup", "/login", "/find/id", "/find/password", "/css/**", "/js/**", "/images/**", "/", "/order-status/**", "/order-status", "/api/orders/**").permitAll()
                .requestMatchers("/api/users/**").hasAnyAuthority("ROLE_USER", "ROLE_OWNER")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(loginSuccessHandler())
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("로그인에 실패했습니다.");
                })
                .permitAll()
            )
            .userDetailsService(userDetailsService)
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"error\": \"로그인이 필요한 서비스입니다.\"}");
                    } else {
                        response.sendRedirect("/login");
                    }
                })
            );

        return http.build();
    }

    private AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            RequestCache requestCache = new HttpSessionRequestCache();
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            
            if (savedRequest != null) {
                String targetUrl = savedRequest.getRedirectUrl();
                if (targetUrl != null && targetUrl.contains("/api/")) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\": true, \"message\": \"로그인 성공\"}");
                    return;
                }
            }
            
            response.sendRedirect("/");
        };
    }
}
