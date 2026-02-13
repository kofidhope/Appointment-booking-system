package com.kofi.booking_system.appointment.model;

import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import com.kofi.booking_system.appointment.enums.TimeSlot;
import com.kofi.booking_system.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"provider_id","appointmentDate","timeSlot"}
        )
})
public class Appointment{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // THE CUSTOMER BOOKING
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id",nullable = false)
    private User customer;

    //THE PROVIDER OFFERING THE SERVICE
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_slot", nullable = false)
    private TimeSlot timeSlot;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

}
