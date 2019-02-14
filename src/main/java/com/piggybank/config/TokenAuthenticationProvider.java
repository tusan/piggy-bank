package com.piggybank.config;

import com.piggybank.users.services.UserAuthenticationService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private UserAuthenticationService userAuthenticationService;

    public TokenAuthenticationProvider(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return Optional
                .ofNullable(authentication.getCredentials())
                .flatMap(token -> userAuthenticationService.authenticateByToken(String.valueOf(token))
                        .map(user -> User.builder()
                                .username(user.username())
                                .password(user.password())
                                .roles("user")
                                .build()))
                .orElseThrow(() -> new BadCredentialsException("Invalid authentication"));
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }
}
