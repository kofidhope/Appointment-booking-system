package com.kofi.booking_system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "users",
            uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
            }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    //private String phoneNumber;

    @Column(nullable = false)
    private boolean enabled;

    private int failedLoginAttempt;
    private LocalDateTime lockUntil;

    @Column(updatable = false,nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate(){
        this.createdAt = LocalDateTime.now();
        //this.enabled = true;
    }
}
