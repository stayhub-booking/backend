package com.ntd7505.stayhub.mapper;

import com.ntd7505.stayhub.dto.request.UserRegisterRequest;
import com.ntd7505.stayhub.dto.response.AdminHotelSummaryResponse;
import com.ntd7505.stayhub.dto.response.AdminUserDetailResponse;
import com.ntd7505.stayhub.dto.response.UserResponse;
import com.ntd7505.stayhub.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = RoleMapper.class)
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  User toUser(UserRegisterRequest request);

  UserResponse toUserResponse(User user);

  AdminUserDetailResponse toAdminUserDetailResponse(User user);

  AdminHotelSummaryResponse.OwnerResponse toOwnerResponse(User user);
}
