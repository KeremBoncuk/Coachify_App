package io.coachify.security;

import io.coachify.entity.jwt.UserRole;
import io.coachify.exception.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
    throws ServletException, IOException {

    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    try {
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        final String token = authHeader.substring(7);

        if (jwtService.isTokenValid(token)) {
          ObjectId userId = jwtService.extractUserId(token);
          UserRole userRole = jwtService.extractUserRole(token);

          CustomPrincipal principal = new CustomPrincipal(userId, userRole);

          UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }

    } catch (JwtAuthenticationException ex) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write(String.format("""
        {
          "error": "JWT ERROR: %s",
          "status": 401
        }
        """, ex.getMessage()));
      return;
    }

    filterChain.doFilter(request, response);
  }
}
