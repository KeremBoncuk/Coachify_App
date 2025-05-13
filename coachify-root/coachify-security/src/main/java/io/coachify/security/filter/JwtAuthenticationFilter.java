package io.coachify.security.filter;

import io.coachify.security.auth.CustomPrincipal;
import io.coachify.security.auth.JwtAuthenticationToken;
import io.coachify.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7); // remove "Bearer "

    if (!jwtService.validateToken(token)) {
      filterChain.doFilter(request, response); // silently skip
      return;
    }

    var userId = jwtService.getUserId(token);
    var role = jwtService.getUserRole(token);
    var tokenId = jwtService.getTokenId(token);

    CustomPrincipal principal = new CustomPrincipal(userId, role, tokenId);
    JwtAuthenticationToken authentication = new JwtAuthenticationToken(principal);

    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }
}
