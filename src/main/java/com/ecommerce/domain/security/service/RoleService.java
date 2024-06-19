package com.ecommerce.domain.security.service;

import com.ecommerce.domain.security.model.Role;
import com.ecommerce.domain.security.model.RoleName;

public interface RoleService {
    Role findByRoleName(RoleName roleName);
}
