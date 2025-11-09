package com.example.the_autumn.util;

import org.springframework.http.ResponseEntity;

public class RoleUtil {

    public static boolean isStaff(String token) {
        return token != null && token.startsWith("STAFF:");
    }

    public static boolean isCustomer(String token) {
        return token != null && token.startsWith("CUSTOMER:");
    }

    public static ResponseEntity<?> checkAdminAccess(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized - Please login");
        }

        String token = authHeader.substring(7);
        if (!isStaff(token)) {
            return ResponseEntity.status(403).body("Forbidden - Admin access required");
        }

        return null;
    }
}