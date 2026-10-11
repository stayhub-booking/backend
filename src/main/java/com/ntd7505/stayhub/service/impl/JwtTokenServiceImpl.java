package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.config.IssuedToken;
import com.ntd7505.stayhub.config.JwtProperties;
import com.ntd7505.stayhub.entity.User;
import com.ntd7505.stayhub.service.JwtTokenService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

  private final JwtEncoder jwtEncoder;
  private final JwtProperties properties;

  @Override
  public IssuedToken generateToken(User user) {
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plus(properties.accessTokenTtl());

    List<String> roles =
        user.getRoles().stream()
            .filter(userRole -> userRole.getRole().isActive())
            .map(userRole -> userRole.getRole().getRoleKey())
            .sorted()
            .toList();

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer(properties.issuer())
            .subject(user.getId().toString())
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .claim("roles", roles)
            .build();

    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

    String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

    return new IssuedToken(token, properties.accessTokenTtl().toSeconds());
  }
}
