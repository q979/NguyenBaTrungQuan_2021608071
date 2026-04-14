package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.enums.Status;
import com.trungquan.nongsan.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user with that email");
        }

        // Check if account is locked (OFFLINE = locked)
        if (user.getStatus() == Status.OFFLINE) {
            throw new UsernameNotFoundException("Tài khoản này đã bị khóa, Vui lòng liên hệ sđt 0123456789 để được hỗ trợ.");
        }

        return user;
    }
}

