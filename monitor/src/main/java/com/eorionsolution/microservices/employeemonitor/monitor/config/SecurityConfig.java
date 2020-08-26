package com.eorionsolution.microservices.employeemonitor.monitor.config;

import com.eorionsolution.microservices.employeemonitor.monitor.repository.AdminRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

import java.net.URI;

@EnableWebFluxSecurity
@Configuration
@AllArgsConstructor
public class SecurityConfig {
    private final AdminRepository adminRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                .pathMatchers("/open/**", "/strategyPassword/**", "/admin/init", "/status/**", "/login", "/css/**", "/js/**", "/webfonts/**", "/img/**", "/favicon.ico","/shift/**")
                .permitAll()
                .pathMatchers("/**")
                .authenticated()
                .and()
                .cors().disable()
                .csrf().disable()
                .formLogin().loginPage("/login").authenticationFailureHandler(authenticationFailureHandler())
                .and().logout().logoutSuccessHandler(logoutSuccessHandler("/login"))
                .and().build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return adminRepository::findByUsername;
    }

    public ServerLogoutSuccessHandler logoutSuccessHandler(String uri) {
        RedirectServerLogoutSuccessHandler successHandler = new RedirectServerLogoutSuccessHandler();
        successHandler.setLogoutSuccessUrl(URI.create(uri));
        return successHandler;
    }

    private ServerAuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler("/strategyPassword/errorPassword", "/strategyPassword/expirePassword", "/strategyPassword/systemPassword");
    }
}
