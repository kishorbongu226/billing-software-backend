package in.kishorbongu.billingsoftware.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import in.kishorbongu.billingsoftware.service.implementation.AppUserDetailsService;
import in.kishorbongu.billingsoftware.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final AppUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("JwtRequestFilter - Incoming request: {}", request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        log.debug("JwtRequestFilter - Authorization header received: {}", authHeader);

        String email = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            log.debug("JwtRequestFilter - Extracted JWT: {}", jwt);

            try {
                email = jwtUtil.extractUsername(jwt);
                log.debug("JwtRequestFilter - Extracted email from JWT: {}", email);
            } catch (Exception e) {
                log.error("JwtRequestFilter - Failed extracting username from JWT", e);
            }
        } else {
            log.debug("JwtRequestFilter - No Bearer token found in header");
        }

        // If we got a username AND there is no security context yet:
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("JwtRequestFilter - Loading UserDetails for: {}", email);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            log.debug("JwtRequestFilter - UserDetails loaded. Authorities: {}", userDetails.getAuthorities());

            boolean isValid = jwtUtil.validateToken(jwt, userDetails);
            log.debug("JwtRequestFilter - Token validation result: {}", isValid);

            if (isValid) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("JwtRequestFilter - Authentication set for user: {}", email);
            } else {
                log.warn("JwtRequestFilter - JWT is INVALID for user: {}", email);
            }
        } else {
            log.debug("JwtRequestFilter - Email null or authentication already present");
        }

        filterChain.doFilter(request, response);
    }
}
