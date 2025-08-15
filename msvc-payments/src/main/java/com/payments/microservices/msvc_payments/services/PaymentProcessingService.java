package com.payments.microservices.msvc_payments.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payments.microservices.msvc_payments.config.PaymentMapper;
import com.payments.microservices.msvc_payments.repositories.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentProcessingService {
private final PaymentRepository paymentRepository;
private final PaymentMapper paymentMapper;
private final PaymentValidationService paymentValidationService;

}
