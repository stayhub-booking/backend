package com.ntd7505.stayhub.validation;

import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.exception.AppException;
import java.util.Locale;

public final class NormalizeOwnerSearch {

  private static final int MAX_SEARCH_LENGTH = 100;

  private NormalizeOwnerSearch() {}

  public static String normalize(String q) {
    if (q == null) {
      return null;
    }

    String search = q.trim();

    if (search.isEmpty() || search.length() > MAX_SEARCH_LENGTH) {
      throw new AppException(ErrorCode.VALIDATION_ERROR);
    }

    return "%"
        + search
            .toLowerCase(Locale.ROOT)
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
        + "%";
  }
}
