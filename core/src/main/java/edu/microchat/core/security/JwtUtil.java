package edu.microchat.core.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration:86400000}") // 24 hours default
  private long expiration;

  private Algorithm getAlgorithm() {
    return Algorithm.HMAC256(secret);
  }

  public String extractUsername(String token) {
    return decodeToken(token).getSubject();
  }

  public List<String> extractRoles(String token) {
    return decodeToken(token).getClaim("roles").asList(String.class);
  }

  public Date extractExpiration(String token) {
    return decodeToken(token).getExpiresAt();
  }

  private DecodedJWT decodeToken(String token) {
    return JWT.require(getAlgorithm()).build().verify(token);
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(UserDetails userDetails) {
    List<String> roles =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    return JWT.create()
        .withSubject(userDetails.getUsername())
        .withClaim("roles", roles)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
        .sign(getAlgorithm());
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
