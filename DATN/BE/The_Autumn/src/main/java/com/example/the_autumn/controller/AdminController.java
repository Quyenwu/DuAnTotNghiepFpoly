package com.example.the_autumn.controller;

import com.example.the_autumn.util.RoleUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> errorResponse = RoleUtil.checkAdminAccess(authHeader);
        if (errorResponse != null) return errorResponse;

        Map<String, Object> data = new HashMap<>();
        data.put("message", "Welcome to Admin Dashboard");
        data.put("stats", Map.of(
                "totalProducts", 150,
                "totalOrders", 45,
                "revenue", 25000000
        ));

        return ResponseEntity.ok(data);
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> errorResponse = RoleUtil.checkAdminAccess(authHeader);
        if (errorResponse != null) return errorResponse;

        Map<String, Object> data = new HashMap<>();
        data.put("message", "Products Management");
        data.put("products", "List of all products...");

        return ResponseEntity.ok(data);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(@RequestHeader("Authorization") String authHeader) {
        ResponseEntity<?> errorResponse = RoleUtil.checkAdminAccess(authHeader);
        if (errorResponse != null) return errorResponse;

        return ResponseEntity.ok("All orders data...");
    }
}