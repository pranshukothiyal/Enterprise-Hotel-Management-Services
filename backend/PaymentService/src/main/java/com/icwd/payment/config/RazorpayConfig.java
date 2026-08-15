package com.icwd.payment.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RazorpayConfig {

    @Bean
    public RazorpayClient razorpayClient(
            @Value("${razorpay.key-id}")
            String keyId,

            @Value("${razorpay.key-secret}")
            String keySecret
    ) throws RazorpayException {

        log.info(
                "Initializing Razorpay client"
        );

        RazorpayClient razorpayClient =
                new RazorpayClient(
                        keyId,
                        keySecret
                );

        log.info(
                "Razorpay client initialized successfully"
        );

        return razorpayClient;
    }
}