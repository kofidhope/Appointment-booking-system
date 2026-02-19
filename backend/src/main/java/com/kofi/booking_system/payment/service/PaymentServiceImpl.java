package com.kofi.booking_system.payment.service;

import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import com.kofi.booking_system.appointment.model.Appointment;
import com.kofi.booking_system.appointment.repository.AppointmentRepository;
import com.kofi.booking_system.common.exception.BadRequestException;
import com.kofi.booking_system.common.exception.ResourceNotFoundException;
import com.kofi.booking_system.payment.dto.CreatePaymentRequest;
import com.kofi.booking_system.payment.dto.PaymentInitResponse;
import com.kofi.booking_system.payment.dto.PaymentResponse;
import com.kofi.booking_system.payment.entity.Payment;
import com.kofi.booking_system.payment.enums.PaymentStatus;
import com.kofi.booking_system.payment.factory.PaymentProviderFactory;
import com.kofi.booking_system.payment.provider.PaymentProvider;
import com.kofi.booking_system.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements  PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentProviderFactory providerFactory;

    @Override
    public PaymentInitResponse createPayment(CreatePaymentRequest request, String customerEmail) {
        //fetch the appointment
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment Not Found"));
        //check if the appointment belongs to the user
        if (!appointment.getCustomer().getEmail().equals(customerEmail)) {
            throw new BadRequestException("You can only pay for your appointment");
        }
        // check if the appointment is confirmed
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException("Appointment must be confirmed before payment");
        }
        // check if payment already exist
        paymentRepository.findByAppointmentId(appointment.getId())
                .ifPresent(p -> {
                    throw new BadRequestException("Payment already exists for this appointment");
                });
        //proceed with the payment
        Payment payment = Payment.builder()
                .appointment(appointment)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        PaymentProvider provider = providerFactory.getProvider(payment.getMethod().name());
        String clientRef = provider.initiate(payment);
        paymentRepository.save(payment);

        return new PaymentInitResponse(payment.getId(),clientRef);
    }

    @Override
    public PaymentResponse confirmPayment(Long paymentId, String providerReference) {
        //fetch payment id if not throw error
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        // pick the right provider
        PaymentProvider provider = providerFactory.getProvider(payment.getMethod().name());
        //confirm via provider
        provider.confirm(payment,providerReference);
        //persist update payment
        paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .appointmentId(payment.getAppointment().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
