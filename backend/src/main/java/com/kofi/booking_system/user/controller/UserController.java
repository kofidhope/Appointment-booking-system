package com.kofi.booking_system.user.controller;

import com.kofi.booking_system.user.dto.UpdateUserRequest;
import com.kofi.booking_system.user.dto.UserResponse;
import com.kofi.booking_system.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication.getName());
    }

    @PutMapping("/me")
    public void updateCurrentUser(Authentication authentication,
                                  @Valid @RequestBody
                                  UpdateUserRequest request){
        userService.updateCurrentUser(authentication.getName(), request);
    }

    @PreAuthorize("hasRole(ADMIN)")
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }
}
