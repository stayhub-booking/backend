package com.ntd7505.stayhub.dto.response.projection;

import java.util.UUID;

public interface LoginCredentialProjection {

  UUID getId();

  String getPasswordHash();
}
