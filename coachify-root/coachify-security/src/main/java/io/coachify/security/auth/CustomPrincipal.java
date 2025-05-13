package io.coachify.security.auth;

import io.coachify.entity.model.UserRole;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomPrincipal implements UserDetails {

  private final ObjectId userId;
  private final UserRole role;
  private final String tokenId;

  public CustomPrincipal(ObjectId userId, UserRole role, String tokenId) {
    this.userId = userId;
    this.role = role;
    this.tokenId = tokenId;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override public String getPassword() { return null; }
  @Override public String getUsername() { return userId.toHexString(); }
  @Override public boolean isAccountNonExpired() { return true; }
  @Override public boolean isAccountNonLocked() { return true; }
  @Override public boolean isCredentialsNonExpired() { return true; }
  @Override public boolean isEnabled() { return true; }
}
