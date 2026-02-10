package com.kofi.booking_system.providerService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class CreateAvailabilityRequest {

  @NotNull
  private DayOfWeek dayOfWeek;

  @NotNull
  private LocalTime startTime;

  @NotNull
  private LocalTime endTime;

}


