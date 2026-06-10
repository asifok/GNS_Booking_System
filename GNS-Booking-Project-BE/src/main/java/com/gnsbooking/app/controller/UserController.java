package com.gnsbooking.app.controller;

import org.springframework.web.bind.annotation.*;

import com.gnsbooking.app.dto.SendOtpRequest;
import com.gnsbooking.app.dto.VerifyOtpRequest;
import com.gnsbooking.app.entity.UserEntity;
import com.gnsbooking.app.serviceI.UserServiceI;

import io.swagger.v3.oas.annotations.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")   // ✅ Allow all origins
public class UserController {

	@Autowired
	UserServiceI userServiceI;

    @Operation(summary = "Send OTP")
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpRequest request) {
    	try {
    		userServiceI.sendOtp(request.getEmail());
            return ResponseEntity.ok("OTP sent successfully");
		} catch (RuntimeException e) {
			return ResponseEntity.ok(e.getMessage());
		}
    }

    @Operation(summary = "Verify OTP and Login")
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
    	try {
        	UserEntity user = userServiceI.verifyOtp(request);
            return ResponseEntity.ok(user.isAdmin());
		} catch (RuntimeException e) {
			return ResponseEntity.ok(e.getMessage());
		}
    }
}
