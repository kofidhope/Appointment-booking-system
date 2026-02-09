package com.kofi.booking_system.user.service;

import com.kofi.booking_system.auth.exception.InvalidCredentialsException;
import com.kofi.booking_system.auth.model.User;
import com.kofi.booking_system.auth.repository.UserRepository;
import com.kofi.booking_system.user.dto.UpdateUserRequest;
import com.kofi.booking_system.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    // fetch current logged in user
    @Override
    public UserResponse getCurrentUser(String email) {
        //1. fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        if (!user.isEnabled()) {
            throw new InvalidCredentialsException("Account not verified");
        }
        return mapToResponse(user);
    }

    @Override
    public void updateCurrentUser(String email, UpdateUserRequest request) {
        //1. fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        //only safe field are updated
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        //user.setPhoneNumber(request.getPhoneNumber);

        userRepository.save(user);
    }

    //admin only fetch by id
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
               // user.getPhoneNumber(),
                user.getRole().name()
        );
    }
}
