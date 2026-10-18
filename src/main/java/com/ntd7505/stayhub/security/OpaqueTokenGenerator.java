package com.ntd7505.stayhub.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import org.springframework.stereotype.Component;

@Component
public class OpaqueTokenGenerator {

  private static final int TOKEN_SIZE_BYTES = 32;

  private final SecureRandom secureRandom = new SecureRandom();

  public GeneratedToken generate() {
    byte[] bytes = new byte[TOKEN_SIZE_BYTES];
    secureRandom.nextBytes(bytes);

    String rawValue = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    return new GeneratedToken(rawValue, hash(rawValue));
  }

  public String hash(String rawValue) {
    try {
      byte[] digest =
          MessageDigest.getInstance("SHA-256").digest(rawValue.getBytes(StandardCharsets.UTF_8));

      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 is unavailable", exception);
    }
  }

  public record GeneratedToken(String rawValue, String hash) {}
}
