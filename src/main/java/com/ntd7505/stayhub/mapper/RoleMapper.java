package com.ntd7505.stayhub.mapper;

import com.ntd7505.stayhub.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {

  default String toRoleKey(UserRole userRole) {
    if (userRole == null) {
      return null;
    }
    return userRole.getRole().getRoleKey();
  }
}
