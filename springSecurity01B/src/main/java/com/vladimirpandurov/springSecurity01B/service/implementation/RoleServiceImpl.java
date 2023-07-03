package com.vladimirpandurov.springSecurity01B.service.implementation;

import com.vladimirpandurov.springSecurity01B.domain.Role;
import com.vladimirpandurov.springSecurity01B.repository.RoleRepository;
import com.vladimirpandurov.springSecurity01B.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByUserId(Long userId) {
        return this.roleRepository.getRoleByUserId(userId);
    }
}
