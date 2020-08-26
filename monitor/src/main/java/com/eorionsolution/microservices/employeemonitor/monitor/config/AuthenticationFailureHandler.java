package com.eorionsolution.microservices.employeemonitor.monitor.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.net.URI;

public class AuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
    private final URI errorUri;
    private final URI expireUri;
    private final URI sysPwdUri;
    private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

    public AuthenticationFailureHandler(String errorUri, String expireUri, String sysPwdUri) {
        this.errorUri = URI.create(errorUri);
        this.expireUri = URI.create(expireUri);
        this.sysPwdUri = URI.create(sysPwdUri);
    }

    public void setRedirectStrategy(ServerRedirectStrategy redirectStrategy) {
        Assert.notNull(redirectStrategy, "redirectStrategy cannot be null");
        this.redirectStrategy = redirectStrategy;
    }

    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            return this.redirectStrategy.sendRedirect(webFilterExchange.getExchange(), this.errorUri);
        }
        if (exception instanceof CredentialsExpiredException) {
            return this.redirectStrategy.sendRedirect(webFilterExchange.getExchange(), this.expireUri);
        }
        if (exception instanceof LockedException) {
            return this.redirectStrategy.sendRedirect(webFilterExchange.getExchange(), this.sysPwdUri);
        }
        return this.redirectStrategy.sendRedirect(webFilterExchange.getExchange(), this.errorUri);
    }
}
