package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.CalculateShippingFeeRequest;
import com.example.the_autumn.model.response.ShippingFeeResponse;
import com.example.the_autumn.service.ShippingCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/van-chuyen")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class ShippingController {

    private final ShippingCalculatorService shippingCalculatorService;

    @PostMapping("/tinh-phi")
    public ResponseEntity<ShippingFeeResponse> calculateShippingFee(
            @RequestBody CalculateShippingFeeRequest request) {
        try {
            ShippingFeeResponse response = shippingCalculatorService.calculateShippingFee(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ShippingFeeResponse errorResponse = new ShippingFeeResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Lỗi tính phí vận chuyển: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/don-vi-van-chuyen")
    public ResponseEntity<?> getShippingProviders() {
        return ResponseEntity.ok(new String[]{"GHN", "GHTK"});
    }
}