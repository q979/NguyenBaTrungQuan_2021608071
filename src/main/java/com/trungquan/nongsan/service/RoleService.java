package com.trungquan.nongsan.service;

import com.trungquan.nongsan.entity.Role;

import java.util.List;

public interface RoleService {
    Role createRole(Role role);

    Role getRoleById(Long roleId);

    List<Role> getAllRoles();

    void deleteRole(Long roleId);
}
