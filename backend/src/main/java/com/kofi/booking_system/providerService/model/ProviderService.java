package com.kofi.booking_system.providerService.model;

import com.kofi.booking_system.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "provider_service")
public class ProviderService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    private String description;

    private Integer durationMinutes;

    private BigDecimal price;

}
