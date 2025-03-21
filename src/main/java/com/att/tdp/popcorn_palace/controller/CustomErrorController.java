package com.att.tdp.popcorn_palace.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);


    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();

        // Get the error status
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            statusCode = HttpStatus.NOT_FOUND.value();
        }

        // Get the error message
        String message = (String) request.getAttribute("javax.servlet.error.message");
        if (message == null || message.isEmpty()) {
            message = "Resource not found";
        }

        // Get the error path
        String path = (String) request.getAttribute("javax.servlet.error.request_uri");
        if (path == null) {
            path = request.getRequestURI();
        }

        // Build the error response
        errorDetails.put("status", statusCode);
        errorDetails.put("message", message);
        errorDetails.put("path", path);
//        errorDetails.put("timestamp", System.currentTimeMillis());

        logger.error("Error {}: {} at {}", statusCode, message, path);
        return new ResponseEntity<>(errorDetails, HttpStatus.valueOf(statusCode));
    }

}
