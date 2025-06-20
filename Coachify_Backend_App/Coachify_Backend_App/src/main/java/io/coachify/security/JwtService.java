package io.coachify.security;

import io.coachify.entity.jwt.ActiveToken;
import io.coachify.entity.jwt.UserRole;
import io.coachify.repo.ActiveTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final ActiveTokenRepository activeTokenRepository;

  @Value("${jwt.secret}")
  private String jwtSecret;

  private Key signingKey;

  private static final long EXPIRATION_MILLIS = 24 * 60 * 60 * 1000; // 1 day

  @PostConstruct
  public void init() {
    this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }


  public String generateToken(ObjectId userId, UserRole role) {
    activeTokenRepository.deleteByUserId(userId);

    String tokenId = UUID.randomUUID().toString();
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plusMillis(EXPIRATION_MILLIS);

    String jwt = Jwts.builder()
      .setSubject(userId.toHexString())
      .setId(tokenId)
      .claim("role", role.name())
      .setIssuedAt(Date.from(issuedAt))
      .setExpiration(Date.from(expiresAt))
      .signWith(signingKey, SignatureAlgorithm.HS256)
      .compact();

    ActiveToken activeToken = new ActiveToken(null, userId, tokenId, role, issuedAt, expiresAt );
    activeTokenRepository.save(activeToken);
    return jwt;

  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = extractAllClaims(token);

      String tokenId = claims.getId();
      Instant expiration = claims.getExpiration().toInstant();

      if (expiration.isBefore(Instant.now())) {
        throw new JwtAuthenticationException("Token is expired.");
      }

      if (!activeTokenRepository.existsByTokenId(tokenId)) {
        throw new JwtAuthenticationException("Token is not recognized (possibly revoked).");
      }

      return true;

    } catch (ExpiredJwtException e) {
      throw new JwtAuthenticationException("Token has expired.", e);
    } catch (UnsupportedJwtException e) {
      throw new JwtAuthenticationException("Unsupported JWT.", e);
    } catch (MalformedJwtException e) {
      throw new JwtAuthenticationException("Malformed JWT.", e);
    } catch (SignatureException e) {
      throw new JwtAuthenticationException("Invalid JWT signature.", e);
    } catch (IllegalArgumentException e) {
      throw new JwtAuthenticationException("JWT is empty or null.", e);
    }
  }



  public ObjectId extractUserId(String token) {
    return new ObjectId(extractAllClaims(token).getSubject());
  }

  public UserRole extractUserRole(String token) {
    return UserRole.valueOf((String) extractAllClaims(token).get("role"));
  }

  public String extractTokenId(String token) {
    return extractAllClaims(token).getId();
  }

  public Instant extractExpiration(String token) {
    return extractAllClaims(token).getExpiration().toInstant();
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
    } catch (JwtException e) {
      throw new JwtAuthenticationException("Failed to extract claims from JWT.", e);
    }
  }




}
