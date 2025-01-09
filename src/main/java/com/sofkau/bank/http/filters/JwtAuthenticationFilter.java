package com.sofkau.bank.http.filters;

import com.sofkau.bank.http.converters.JwtAuthenticationConverter;
import com.sofkau.bank.services.tokens.BlacklistedTokenService;
import com.sofkau.bank.utils.jwt.JwtInterpreter;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationConverter authenticationConverter;
    private final UserDetailsService userDetailsService;
    private final BlacklistedTokenService blacklistedTokenService;

    private final HandlerExceptionResolver handlerExceptionResolver;

    private JwtAuthenticationFilter(
            JwtAuthenticationConverter authenticationConverter,
            UserDetailsService userDetailsService, BlacklistedTokenService blacklistedTokenService,
            HandlerExceptionResolver handlerExceptionResolver) {
        this.authenticationConverter = authenticationConverter;
        this.userDetailsService = userDetailsService;
        this.blacklistedTokenService = blacklistedTokenService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) {
        try {
            Authentication auth = authenticationConverter.convert(request);

            if (auth == null) {
                this.logger.trace("Did not process authentication request since failed to find valid token in Authorization header");
                filterChain.doFilter(request, response);
                return;
            }

            if (blacklistedTokenService.isBlacklisted(auth.getName()))
                filterChain.doFilter(request, response);

            UserDetails client = userDetailsService
                    .loadUserByUsername(auth.getName());

            UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken
                    .authenticated(client, auth.getCredentials(), client.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                String token = JwtInterpreter
                        .extractFromHeader(request.getHeader(HttpHeaders.AUTHORIZATION));

                blacklistedTokenService.addToBlacklist(token);
            }

            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
