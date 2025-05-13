package io.coachify.security.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private final CustomPrincipal principal;

  public JwtAuthenticationToken(CustomPrincipal principal) {
    super(principal.getAuthorities());
    this.principal = principal;
    setAuthenticated(true);
  }
  @Override
  public Object getCredentials() {
    return null; // Not needed for JWT
  }
  @Override
  public Object getPrincipal() {
    return principal;
  }
}
