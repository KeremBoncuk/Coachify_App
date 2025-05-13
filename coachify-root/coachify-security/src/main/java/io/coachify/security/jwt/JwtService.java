package io.coachify.security.jwt;

import io.coachify.entity.model.ActiveToken;
import io.coachify.entity.model.UserRole;
import io.coachify.entity.repo.ActiveTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
  private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 1 day
  private static final long CLOCK_SKEW_SECONDS = 60;

  @PostConstruct
  private void init() {
    this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  public String generateToken(ObjectId userId, UserRole role) {
    // 1. Invalidate all previous tokens
    activeTokenRepository.deleteAllByUserId(userId);

    // 2. Generate new tokenId
    String tokenId = UUID.randomUUID().toString();

    // 3. Set expiration date
    Instant now = Instant.now();
    Instant expiration = now.plusMillis(EXPIRATION_MS);

    // 4. Build the JWT
    String jwt = Jwts.builder()
      .setSubject(userId.toHexString())
      .claim("tokenId", tokenId)
      .claim("role", role.name())
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(expiration))
      .signWith(signingKey, SignatureAlgorithm.HS256)
      .compact();

    // 5. Store token in database
    ActiveToken activeToken = new ActiveToken(tokenId, userId, expiration);
    activeTokenRepository.save(activeToken);

    return jwt;
  }

  public boolean validateToken(String token) {
    try {
      // 1. Parse and validate token structure and signature
      Claims claims = Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
        .build()
        .parseClaimsJws(token)
        .getBody();

      // 2. Extract tokenId from claims
      String tokenId = claims.get("tokenId", String.class);
      if (tokenId == null) {
        return false;
      }

      // 3. Check tokenId against database
      return activeTokenRepository.findByTokenId(tokenId)
        .filter(activeToken -> activeToken.getExpiration().isAfter(Instant.now()))
        .isPresent();

    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public ObjectId getUserId(String token) {
    String subject = parseClaims(token).getSubject();
    return new ObjectId(subject);
  }

  public UserRole getUserRole(String token) {
    String roleName = parseClaims(token).get("role", String.class);
    return UserRole.valueOf(roleName);
  }

  public String getTokenId(String token) {
    return parseClaims(token).get("tokenId", String.class);
  }

  public Instant getExpiration(String token) {
    Date expirationDate = parseClaims(token).getExpiration();
    return expirationDate.toInstant();
  }


  public void invalidateToken(ObjectId userId) {
    activeTokenRepository.deleteAllByUserId(userId);
  }



  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(signingKey)
      .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
      .build()
      .parseClaimsJws(token)
      .getBody();
  }



}

