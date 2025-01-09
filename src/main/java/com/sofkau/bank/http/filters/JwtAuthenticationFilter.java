package com.sofkau.bank.http.filters;

import com.sofkau.bank.entities.Client;
import com.sofkau.bank.http.converters.JwtAuthenticationConverter;
import com.sofkau.bank.services.sessions.SessionService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationConverter authenticationConverter;
    private final UserDetailsService userDetailsService;
    private final SessionService sessionService;

    private final HandlerExceptionResolver handlerExceptionResolver;

    private JwtAuthenticationFilter(
            JwtAuthenticationConverter authenticationConverter,
            UserDetailsService userDetailsService, SessionService sessionService,
            HandlerExceptionResolver handlerExceptionResolver) {
        this.authenticationConverter = authenticationConverter;
        this.userDetailsService = userDetailsService;
        this.sessionService = sessionService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication auth = authenticationConverter.convert(request);

            if (auth == null) {
                this.logger.trace("Did not process authentication request since failed to find valid token in Authorization header");
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails client = userDetailsService
                    .loadUserByUsername(auth.getName());

            UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken
                    .authenticated(client, auth.getCredentials(), client.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException jwt) {
                String token = request.getHeader(HttpHeaders.AUTHORIZATION).trim()
                        .substring(7).trim();

                sessionService.findSessionByToken(token)
                        .ifPresent(sessionService::markSessionAsBlacklisted);
            }

            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
