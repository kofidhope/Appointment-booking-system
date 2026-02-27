package com.kofi.booking_system.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kofi.booking_system.appointment.dto.CreateAppointmentRequest;
import com.kofi.booking_system.appointment.enums.TimeSlot;
import com.kofi.booking_system.appointment.repository.AppointmentRepository;
import com.kofi.booking_system.appointment.service.EmailTemplateService;
import com.kofi.booking_system.appointment.service.NotificationService;
import com.kofi.booking_system.auth.service.JwtService;
import com.kofi.booking_system.user.model.Role;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppointmentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private EmailTemplateService emailTemplateService;

    private User customer;
    private User provider;

    private String customerToken;
    private String providerToken;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        userRepository.deleteAll();

        customer = userRepository.save(
                User.builder()
                        .email("customer@test.com")
                        .password(passwordEncoder.encode("password"))
                        .firstName("Test")
                        .lastName("Customer")
                        .phoneNumber("0240000000")
                        .role(Role.CUSTOMER)
                        .enabled(true)
                        .failedLoginAttempt(0)
                        .build()
        );

        provider = userRepository.save(
                User.builder()
                        .email("provider@test.com")
                        .password(passwordEncoder.encode("password"))
                        .firstName("Service")
                        .lastName("Provider")
                        .phoneNumber("0240000001")
                        .role(Role.SERVICE_PROVIDER)
                        .enabled(true)
                        .failedLoginAttempt(0)
                        .build()
        );

        customerToken = "Bearer " + jwtService.generateToken(customer.getEmail(), customer.getRole().name());
        providerToken = "Bearer " + jwtService.generateToken(provider.getEmail(), provider.getRole().name());

        // 🔥 Mock external side effects
        doNothing().when(notificationService).sendEmail(any(), any(), any());
        when(emailTemplateService.renderNewBooking(any())).thenReturn("test-email");
    }

    @Test
    void customerCanBookAppointment() throws Exception {

        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.setProviderId(provider.getId());
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setTimeSlot(TimeSlot.SLOT_09_00);

        mockMvc.perform(post("/api/v1/appointment")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())   // ADD THIS
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void providerCannotBookAppointment() throws Exception {

        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.setProviderId(provider.getId());
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setTimeSlot(TimeSlot.SLOT_09_00);

        mockMvc.perform(post("/api/v1/appointment")
                        .header("Authorization", providerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())   // ADD THIS
                .andExpect(status().isInternalServerError());
    }

    @Test
    void bookingWithoutTokenShouldFail() throws Exception {

        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.setProviderId(provider.getId());
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setTimeSlot(TimeSlot.SLOT_09_00);

        mockMvc.perform(post("/api/v1/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())   // ADD THIS
                .andExpect(status().isForbidden()); // Spring Security returns 403 when no token
    }
}