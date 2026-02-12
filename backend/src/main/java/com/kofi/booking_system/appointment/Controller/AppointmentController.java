package com.kofi.booking_system.appointment.Controller;

import com.kofi.booking_system.appointment.dto.AppointmentResponse;
import com.kofi.booking_system.appointment.dto.CreateAppointmentRequest;
import com.kofi.booking_system.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @RequestBody @Valid CreateAppointmentRequest request, Authentication authentication ){
        AppointmentResponse response = appointmentService.bookAppointment(request,authentication.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<AppointmentResponse> confirmAppointment(
            @PathVariable Long id,
            Authentication authentication
    ){
        AppointmentResponse response = appointmentService.confirmAppointment(id,authentication.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<AppointmentResponse> rejectAppointment(
            @PathVariable Long id,
            Authentication authentication
    ){
        AppointmentResponse response = appointmentService.rejectAppointment(id,authentication.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/availability/{providerId}")
    public ResponseEntity<?> getAvailability(
            @PathVariable Long providerId,
            @RequestParam LocalDate date
    ) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(providerId, date));
    }


}
