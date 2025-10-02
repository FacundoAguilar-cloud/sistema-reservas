package com.payments.microservices.msvc_payments.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payments.microservices.msvc_payments.dto.QrGenerationResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/qr")
@Slf4j
public class QRPaymentController {

@PostMapping("/generate")
public ResponseEntity <QrGenerationResponse> genrateQR(@RequestBody QrGenerationResponse response) {
    //TODO: process POST request
    
    return null;
}
    

}
