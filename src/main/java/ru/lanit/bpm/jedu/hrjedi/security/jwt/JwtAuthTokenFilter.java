/*
 * Copyright (c) 2008-2020
 * LANIT
 * All rights reserved.
 *
 * This product and related documentation are protected by copyright and
 * distributed under licenses restricting its use, copying, distribution, and
 * decompilation. No part of this product or related documentation may be
 * reproduced in any form by any means without prior written authorization of
 * LANIT and its licensors, if any.
 *
 * $
 */
package ru.lanit.bpm.jedu.hrjedi.security.jwt;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.impl.identity.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.lanit.bpm.jedu.hrjedi.security.service.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class JwtAuthTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthTokenFilter.class);

    @Autowired
    private JwtProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private IdentityService camundaIdentityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            String jwt = getJwt(request);
            if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
                String username = tokenProvider.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                Authentication camundaAuthentication = new Authentication(
                    userDetails.getUsername(),
                    userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
                );
                camundaIdentityService.setAuthentication(camundaAuthentication);
            }
        } catch (Exception e) {
            LOGGER.error("Can NOT set user authentication -> Message: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }

        return null;
    }
}
