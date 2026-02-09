package com.kofi.booking_system.auth.service;

import com.kofi.booking_system.auth.exception.InvalidCredentialsException;
import com.kofi.booking_system.auth.model.RefreshToken;
import com.kofi.booking_system.auth.model.User;
import com.kofi.booking_system.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshToken create(User user){

        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(7));
        token.setRevokedAt(null);

        return repository.save(token);
    }

    // validate refresh token
    public RefreshToken verify(String tokenValue){
        RefreshToken token = repository.findByToken(tokenValue)
                .orElseThrow(()-> new InvalidCredentialsException("Invalid refresh token"));
        // check if the token is not revoked
        if (token.getRevokedAt() != null){
            throw new InvalidCredentialsException("Refresh token revoked");
        }
        // check expiry
        if (token.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new InvalidCredentialsException("Refresh token expired");
        }

        return token;
    }

    //revoke token(Logout)

    public RefreshToken revoke(RefreshToken token){
        token.setRevokedAt(LocalDateTime.now());
       return repository.save(token);
    }

}
