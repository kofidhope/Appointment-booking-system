package com.kofi.booking_system.appointment.service;

import java.util.Map;

public interface NotificationService {

    void sendEmail(String to, String subject,String body);

    void sendSms(String phone,String message);
}
