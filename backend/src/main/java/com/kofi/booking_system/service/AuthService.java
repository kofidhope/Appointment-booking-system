package com.kofi.booking_system.service;

import com.kofi.appointmentbookingsystem.exception.InvalidCredentialsException;
import com.kofi.appointmentbookingsystem.exception.ResourceAlreadyExistsException;
import com.kofi.booking_system.dto.AuthResponse;
import com.kofi.booking_system.dto.LoginRequest;
import com.kofi.booking_system.dto.RegisterRequest;
import com.kofi.booking_system.model.User;
import com.kofi.booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest request){
        //1.check if the email exist
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use");
        }
        //.create the user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(false)
                .build();
        //3.save the user
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request){
        //1. find the email if exist or throw and error
        User user = (userRepository.findByEmail(request.getEmail()))
                .orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));
        // check if the password matches
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }
        // 3. generate JWT token with subject and role
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        //4. build a response object
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setRole(user.getRole().name());

        return response;
    }
}
