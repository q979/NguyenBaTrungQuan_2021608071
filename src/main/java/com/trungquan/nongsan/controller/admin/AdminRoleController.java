package com.trungquan.nongsan.controller.admin;

import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/admin/roles_management")
public class AdminRoleController extends BaseController {
    private final RoleService roleService;
}

