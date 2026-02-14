package com.kofi.booking_system.user.service;

import com.kofi.booking_system.common.exception.ForbiddenActionException;
import com.kofi.booking_system.common.exception.InvalidCredentialsException;
import com.kofi.booking_system.common.exception.ResourceNotFoundException;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.UserRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.isEnabled()) {
            throw new ForbiddenActionException("Account not verified");
        }
        return mapToResponse(user);
    }

    @Override
    public void updateCurrentUser(String email, UpdateUserRequest request) {
        //1. fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
