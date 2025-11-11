package com.example.the_autumn.util;

import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;

public class RoleUtil {

    public static String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    public static String getUserType(String token) {
        if (token == null || !token.contains(":")) return null;
        return token.split(":")[0]; // STAFF hoặc CUSTOMER
    }

    public static String getUserRole(String token) {
        if (token == null || !token.contains(":")) return null;
        String[] parts = token.split(":");
        return parts.length > 3 ? parts[3] : "STAFF";
    }

    public static Integer getUserId(String token) {
        if (token == null || !token.contains(":")) return null;
        try {
            return Integer.parseInt(token.split(":")[1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean hasRole(String token, String... allowedRoles) {
        String userRole = getUserRole(token);
        return userRole != null && Arrays.asList(allowedRoles).contains(userRole);
    }

    public static ResponseEntity<?> checkAdminAccess(String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(401).body("Unauthorized - Please login");
        }

        if (!"STAFF".equals(getUserType(token))) {
            return ResponseEntity.status(403).body("Forbidden - Staff access required");
        }

        if (!hasRole(token, "ADMIN")) {
            return ResponseEntity.status(403).body("Forbidden - Admin role required");
        }

        return null;
    }

    public static ResponseEntity<?> checkManagerAccess(String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(401).body("Unauthorized - Please login");
        }

        if (!"STAFF".equals(getUserType(token))) {
            return ResponseEntity.status(403).body("Forbidden - Staff access required");
        }

        if (!hasRole(token, "ADMIN", "MANAGER")) {
            return ResponseEntity.status(403).body("Forbidden - Manager role required");
        }

        return null;
    }

    public static ResponseEntity<?> checkStaffAccess(String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(401).body("Unauthorized - Please login");
        }

        if (!"STAFF".equals(getUserType(token))) {
            return ResponseEntity.status(403).body("Forbidden - Staff access required");
        }

        return null;
    }

    public static ResponseEntity<?> checkCustomerOrStaffAccess(String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(401).body("Unauthorized - Please login");
        }

        String userType = getUserType(token);
        if (!"CUSTOMER".equals(userType) && !"STAFF".equals(userType)) {
            return ResponseEntity.status(403).body("Forbidden - Login required");
        }

        return null;
    }

    public static ResponseEntity<?> checkCustomerAccess(String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(401).body("Unauthorized - Please login");
        }

        if (!"CUSTOMER".equals(getUserType(token))) {
            return ResponseEntity.status(403).body("Forbidden - Customer access required");
        }

        return null;
    }
}