package com.kofi.booking_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
		"TWILIO_ACCOUNT_SID=test",
		"TWILIO_AUTH_TOKEN=test",
		"TWILIO_PHONE_NUMBER=+1234567890",
		"STRIPE_SECRET_KEY=test",
		"PAYSTACK_SECRET_KEY=test",
		"PAYSTACK_PUBLIC_KEY=test",
		"JWT_SECRET=dGVzdFNlY3JldEtleUZvclRlc3RpbmdQdXJwb3Nlc09ubHk=",
		"JWT_EXPIRATION=3600000"
})
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
