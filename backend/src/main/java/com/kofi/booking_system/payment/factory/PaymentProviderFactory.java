package com.kofi.booking_system.payment.factory;

import com.kofi.booking_system.payment.provider.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentProviderFactory {

    private final List<PaymentProvider> providers;

    public PaymentProvider getProvider(String method) {
        return providers.stream()
                .filter(p -> p.supports(method))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No provider for " + method));
    }

}
