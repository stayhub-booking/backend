package com.ntd7505.stayhub.validation;

import com.ntd7505.stayhub.enums.ErrorCode;
import com.ntd7505.stayhub.exception.AppException;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableValidator {

  private static final int MAX_PAGE_SIZE = 100;

  private PageableValidator() {}

  public static Pageable validate(
      Pageable pageable, Set<String> allowedSortFields, Sort defaultSort) {

    if (pageable.isUnpaged()
        || pageable.getPageNumber() < 0
        || pageable.getPageSize() < 1
        || pageable.getPageSize() > MAX_PAGE_SIZE) {
      throw new AppException(ErrorCode.VALIDATION_ERROR);
    }

    Sort sort = pageable.getSort();

    for (Sort.Order order : sort) {
      if (!allowedSortFields.contains(order.getProperty())) {
        throw new AppException(ErrorCode.VALIDATION_ERROR);
      }
    }

    if (sort.isUnsorted()) {
      sort = defaultSort;
    }

    if (sort.getOrderFor("id") == null) {
      sort = sort.and(Sort.by("id"));
    }

    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
  }
}
