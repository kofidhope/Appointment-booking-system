package com.kofi.booking_system.providerService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CreateServiceRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Duration is required")
    private Integer durationMinutes;

    @NotNull(message = "Price is required")
    private BigDecimal price;

}
