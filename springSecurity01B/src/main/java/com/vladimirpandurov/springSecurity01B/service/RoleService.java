package com.vladimirpandurov.springSecurity01B.service;

import com.vladimirpandurov.springSecurity01B.domain.Role;

public interface RoleService {


    Role getRoleByUserId(Long userId);
}
