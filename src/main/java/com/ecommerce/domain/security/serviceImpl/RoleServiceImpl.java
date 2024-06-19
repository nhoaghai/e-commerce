package com.ecommerce.domain.security.serviceImpl;

import com.ecommerce.common.exception.DomainException;
import com.ecommerce.domain.security.model.Role;
import com.ecommerce.domain.security.model.RoleName;
import com.ecommerce.domain.security.repository.RoleRepository;
import com.ecommerce.domain.security.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    @Override
    public Role findByRoleName(RoleName roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(()-> DomainException.notFound(roleName.name()));
    }
}
