package com.kofi.booking_system.user.service;

import com.kofi.booking_system.user.dto.UpdateUserRequest;
import com.kofi.booking_system.user.dto.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(String email);

    void updateCurrentUser(String email, UpdateUserRequest request);

    UserResponse getUserById(Long id);

}
