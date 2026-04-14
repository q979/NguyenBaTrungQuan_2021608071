package com.trungquan.nongsan.service;

import com.trungquan.nongsan.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<User> getAllUserOrderByCreatedDate(Pageable pageable);

    User getUserById(Long userId);

    List<User> getAllUsers();

    void updateUser(User user);

    void deleteUser(Long userId);

    boolean registerUser(User user);

    void deleteUserById(Long id);

    void saveUser(User user);

    void addProductToUser(Long userId, Long ProductId);

    void removeProductFromUser(Long userId, Long ProductId);

    Long countUser();

    void saveUserForWebSocket(User user);

    void disconnectUser(User user);

    List<User> findConnedUsers();

}
