package com.trungquan.nongsan.service;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

public interface PaypalService {

    Payment createPayment(
            Double totalAmount,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl
    ) throws PayPalRESTException;

    Payment executePayment(
            String paymentId,
            String payerId
    ) throws PayPalRESTException;

}
