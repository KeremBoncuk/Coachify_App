package io.coachify.security;

import io.coachify.entity.jwt.UserRole;
import org.bson.types.ObjectId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomPrincipal implements UserDetails {

  private final ObjectId userId;
  private final UserRole role;

  public CustomPrincipal(ObjectId userId, UserRole role) {
    this.userId = userId;
    this.role = role;
  }

  public ObjectId getUserId() {
    return userId;
  }

  public UserRole getRole() {
    return role;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getPassword() {
    return null; // Not used in JWT flow
  }

  @Override
  public String getUsername() {
    return userId.toHexString(); // Optional, used for logging
  }

  @Override
  public boolean isAccountNonExpired() {
    return true; // Default behavior
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
