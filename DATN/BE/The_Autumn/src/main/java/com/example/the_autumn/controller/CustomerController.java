// src/main/java/com/example/the_autumn/controller/CustomerController.java
package com.example.the_autumn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String token = authHeader.substring(7);
        if (!token.startsWith("STAFF:") && !token.startsWith("CUSTOMER:")) {
            return ResponseEntity.status(403).body("Forbidden - Login required");
        }

        return ResponseEntity.ok("Customer Profile Data");
    }
}