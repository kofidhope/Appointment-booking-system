package com.kofi.booking_system.appointment.service;

import com.kofi.booking_system.appointment.model.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {
    private final SpringTemplateEngine templateEngine;

    public String renderNewBooking(Appointment appointment) {

        Context context = new Context();
        context.setVariable("providerFirstName", appointment.getProvider().getFirstName());
        context.setVariable("date", appointment.getAppointmentDate());
        context.setVariable("timeSlot", appointment.getTimeSlot());
        context.setVariable("customerName",
                appointment.getCustomer().getFirstName() + " " +
                        appointment.getCustomer().getLastName()
        );

        return templateEngine.process("new-booking", context);
    }

    public String renderBookingConfirmed(Appointment appointment) {

        Context context = new Context();
        context.setVariable("customerFirstName", appointment.getCustomer().getFirstName());
        context.setVariable("date", appointment.getAppointmentDate());
        context.setVariable("timeSlot", appointment.getTimeSlot());
        context.setVariable("providerName",
                appointment.getProvider().getFirstName() + " " +
                        appointment.getProvider().getLastName()
        );

        return templateEngine.process("booking-confirmed", context);
    }

    public String renderBookingCancelled(Appointment appointment) {

        Context context = new Context();
        context.setVariable("date", appointment.getAppointmentDate());
        context.setVariable("timeSlot", appointment.getTimeSlot());
//        context.setVariable("cancelledBy", cancelledBy);

        return templateEngine.process("booking-cancelled", context);
    }

    public String renderAppointmentExpired(Appointment appointment) {
        Context context = new Context();
        context.setVariable("customerName",
                appointment.getCustomer().getFirstName() + " " +
                        appointment.getCustomer().getLastName()
        );
        context.setVariable("providerName",
                appointment.getProvider().getFirstName() + " " +
                        appointment.getProvider().getLastName()
        );
        context.setVariable("date", appointment.getAppointmentDate());
        context.setVariable("timeSlot", appointment.getTimeSlot());

        return templateEngine.process("appointment-expired", context);
    }
}
