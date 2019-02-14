package com.piggybank.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer";

    TokenAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) {

        String token = Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .map(v -> v.replace(BEARER, "").trim())
                .orElseThrow(() -> new BadCredentialsException("Missing authentication token."));

        Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
        return getAuthenticationManager().authenticate(auth);
    }
}
