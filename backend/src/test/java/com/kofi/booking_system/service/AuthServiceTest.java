package com.kofi.booking_system.service;

import com.kofi.booking_system.auth.dto.LoginRequest;
import com.kofi.booking_system.auth.dto.RegisterRequest;
import com.kofi.booking_system.auth.service.AuthService;
import com.kofi.booking_system.auth.service.JwtService;
import com.kofi.booking_system.auth.service.OtpService;
import com.kofi.booking_system.auth.service.RefreshTokenService;
import com.kofi.booking_system.common.exception.ForbiddenActionException;
import com.kofi.booking_system.common.exception.InvalidCredentialsException;
import com.kofi.booking_system.common.exception.ResourceAlreadyExistsException;
import com.kofi.booking_system.user.model.RefreshToken;
import com.kofi.booking_system.user.model.Role;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.TokenRepository;
import com.kofi.booking_system.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //enables mockito annotations
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private OtpService otpService;
    @Mock
    private RefreshTokenService refreshTokenService;

    // this is the class we are testing
    @InjectMocks
    private AuthService authService;

    private User user; // holds fake test user

    @BeforeEach  // this annotation runs this method before every single test method
    void setUp() {
        // create a fake user object for tests
        user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .password("hashed-password")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
    }

    //test: register success
    @Test
    void register_shouldSucceed_whenEmailNotExist() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@mail.com");
        request.setPassword("hashed-password");
        request.setFirstName("John");
        request.setLastName("Doe");

        //fake repository behaviour
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        //call method
        authService.register(request);

        //verify interaction
        verify(userRepository).save(any(User.class)); // user was saved
        verify(otpService).sendValidationEmail(any(User.class)); // otp email sent
    }

    //test: register fails if email exist already
    @Test
    void register_shouldFail_whenEmailAlreadyExist() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, ()->{
           authService.register(request);
        });
        verify(userRepository, never()).save(any());
    }

    //test: login success
    @Test
    void login_shouldSucceed_whenCredentialsAreCorrect() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail");
        request.setPassword("123456");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(),user.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(anyString(),anyString()))
                .thenReturn("fake-jwt");
        // mock refresh token
        RefreshToken mockRefreshToken = RefreshToken.builder()
                .user(user)
                .token("mock-refresh-token")
                .build();
        when(refreshTokenService.create(user))
                .thenReturn(mockRefreshToken);
        var response = authService.login(request);
        assertNotNull(response);
        assertEquals("fake-jwt", response.getToken());
    }

    //test: login failed if not verified
    @Test
    void login_shouldFail_whenUserNotEnabled() {
        user.setEnabled(false);
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail");
        request.setPassword("12345");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(ForbiddenActionException.class, () -> {
            authService.login(request);
        });
    }

    //test:login fails if password is wrong
    @Test
    void login_shouldFail_whenPasswordIsWrong() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("wrong");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(request);
        });
    }
}

