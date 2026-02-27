package com.kofi.booking_system.appointment.service;

import com.kofi.booking_system.appointment.dto.CreateAppointmentRequest;
import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import com.kofi.booking_system.appointment.enums.TimeSlot;
import com.kofi.booking_system.appointment.model.Appointment;
import com.kofi.booking_system.appointment.repository.AppointmentRepository;
import com.kofi.booking_system.audit.service.AuditLogService;
import com.kofi.booking_system.common.exception.BadRequestException;
import com.kofi.booking_system.common.exception.BookingConflictException;
import com.kofi.booking_system.common.exception.ForbiddenActionException;
import com.kofi.booking_system.user.model.Role;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailTemplateService emailTemplateService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private User customer;
    private User provider;

    @BeforeEach
    void setUp() {
        customer = User.builder()
                .id(1L)
                .email("customer@mail.com")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        provider = User.builder()
                .id(2L)
                .email("provider@mail.com")
                .role(Role.SERVICE_PROVIDER)
                .enabled(true)
                .build();
    }

    @Test
    void bookAppointment_shouldSucceed_whenSlotIsFree() {
        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.setProviderId(provider.getId());
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setTimeSlot(TimeSlot.SLOT_09_00);

        when(userRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));

        when(userRepository.findById(provider.getId()))
                .thenReturn(Optional.of(provider));

        // 👇 Slot is free → repository returns empty
        when(appointmentRepository.existsByProviderAndAppointmentDateAndTimeSlotAndStatusIn(
                any(), any(), any(),any()
        )).thenReturn(false);

        // 👇 stub template rendering if needed
        when(emailTemplateService.renderNewBooking(any(Appointment.class)))
                .thenReturn("email-body");

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = appointmentService.bookAppointment(request, customer.getEmail());

        assertNotNull(response);
        assertEquals(AppointmentStatus.PENDING, response.getStatus());
    }

    @Test
    void bookAppointment_shouldFail_whenSlotAlreadyBooked() {
        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.setProviderId(provider.getId());
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setTimeSlot(TimeSlot.SLOT_09_00);

        when(userRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));

        when(userRepository.findById(provider.getId()))
                .thenReturn(Optional.of(provider));

        // 👇 Slot already taken
        when(appointmentRepository.existsByProviderAndAppointmentDateAndTimeSlotAndStatusIn(
                any(), any(), any(),any()
        )).thenReturn(true);

        assertThrows(BookingConflictException.class, () -> {
            appointmentService.bookAppointment(request, customer.getEmail());
        });
    }

    @Test
    void confirmAppointment_shouldFail_whenProviderDoesNotOwnAppointment() {
        Appointment appointment = Appointment.builder()
                .id(1L)
                .provider(provider)
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        // 👇 Logged-in provider is different
        String otherProviderEmail = "hacker@mail.com";

        assertThrows(ForbiddenActionException.class, () -> {
            appointmentService.confirmAppointment(1L, otherProviderEmail);
        });
    }

    @Test
    void confirmAppointment_shouldSucceed_whenProviderOwnsAppointment() {
        Appointment appointment = Appointment.builder()
                .id(1L)
                .provider(provider)
                .customer(customer)
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = appointmentService.confirmAppointment(1L, provider.getEmail());

        assertEquals(AppointmentStatus.CONFIRMED, response.getStatus());
    }

    @Test
    void cancelAppointment_shouldFail_whenCustomerNotOwner() {
        Appointment appointment = Appointment.builder()
                .id(1L)
                .customer(customer)
                .appointmentDate(LocalDate.now().plusDays(1))
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        String otherCustomer = "other@mail.com";

        assertThrows(ForbiddenActionException.class, () -> {
            appointmentService.cancelAppointment(1L, otherCustomer, "CUSTOMER");
        });
    }

    @Test
    void cancelAppointment_shouldFail_whenPastDate() {
        Appointment appointment = Appointment.builder()
                .id(1L)
                .customer(customer)
                .appointmentDate(LocalDate.now().minusDays(1))
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        assertThrows(BadRequestException.class, () -> {
            appointmentService.cancelAppointment(1L, customer.getEmail(), "CUSTOMER");
        });
    }
}

