package com.trungquan.nongsan.controller.admin;

import com.trungquan.nongsan.entity.Role;
import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.controller.common.BaseController;
import com.trungquan.nongsan.service.RoleService;
import com.trungquan.nongsan.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Controller
@RequestMapping("/admin/users_management")
public class AdminUserController extends BaseController {

    private UserService userService;
    private RoleService roleService;

    @GetMapping
    public String getUsersPage(@RequestParam(name = "page", defaultValue = "1") int page,
                               Model model) {
        int pageSize = 6;
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<User> usersPage = userService.getAllUserOrderByCreatedDate(pageable);
        model.addAttribute("users", usersPage);
        return "admin/user";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users_management";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        // Build list of selected role IDs for checkbox rendering
        java.util.List<Long> selectedRoleIds = new java.util.ArrayList<>();
        if (user.getRoles() != null) {
            for (Role r : user.getRoles()) {
                selectedRoleIds.add(r.getId());
            }
        }
        model.addAttribute("selectedRoleIds", selectedRoleIds);
        return "admin/user_edit";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "roleIds", required = false) Set<Long> roleIds,
                             RedirectAttributes redirectAttributes) {
        User existingUser = userService.getUserById(user.getId());
        if (existingUser == null) {
            return "redirect:/admin/users_management";
        }

        existingUser.setFullName(user.getFullName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setAddress(user.getAddress());
        existingUser.setStatus(user.getStatus());

        // Update roles
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> newRoles = new HashSet<>();
            for (Long roleId : roleIds) {
                Role role = roleService.getRoleById(roleId);
                if (role != null) {
                    newRoles.add(role);
                }
            }
            existingUser.setRoles(newRoles);
        }

        userService.saveUser(existingUser);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");
        return "redirect:/admin/users_management";
    }

    @GetMapping("/delete_user/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Người dùng đã được xóa thành công.");
        return "redirect:/admin/users_management";
    }
}
