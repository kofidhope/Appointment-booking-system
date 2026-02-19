package com.kofi.booking_system.payment.provider;

import com.kofi.booking_system.payment.entity.Payment;

public interface PaymentProvider {

    boolean supports(String method);

    //start payment
    String initiate(Payment payment);

    //verify payment Result(webhook)
    void confirm(Payment payment, String providerReference);

}
