// src/main/java/com/example/the_autumn/controller/CustomerController.java
package com.example.the_autumn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
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