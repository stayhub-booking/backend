package com.ntd7505.stayhub.dto.response.common;

import java.util.List;

public record PageResponse<T>(
    List<T> items, int page, int size, long totalElements, int totalPages) {}
