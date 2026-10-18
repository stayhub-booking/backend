package com.ntd7505.stayhub.security;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Base64;
import org.junit.jupiter.api.Test;

class OpaqueTokenGeneratorTest {

  private final OpaqueTokenGenerator tokenGenerator = new OpaqueTokenGenerator();

  @Test
  void generateShouldReturnRandomUrlSafeTokenAndItsHash() {
    OpaqueTokenGenerator.GeneratedToken first = tokenGenerator.generate();
    OpaqueTokenGenerator.GeneratedToken second = tokenGenerator.generate();

    assertAll(
        () -> assertEquals(32, Base64.getUrlDecoder().decode(first.rawValue()).length),
        () -> assertEquals(64, first.hash().length()),
        () -> assertEquals(tokenGenerator.hash(first.rawValue()), first.hash()),
        () -> assertNotEquals(first.rawValue(), second.rawValue()),
        () -> assertNotEquals(first.hash(), second.hash()));
  }

  @Test
  void hashShouldUseSha256() {
    assertEquals(
        "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
        tokenGenerator.hash("abc"));
  }
}
