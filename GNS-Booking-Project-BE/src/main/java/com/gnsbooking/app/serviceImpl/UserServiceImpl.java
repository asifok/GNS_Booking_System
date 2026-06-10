package com.gnsbooking.app.serviceImpl;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.gnsbooking.app.dto.VerifyOtpRequest;
import com.gnsbooking.app.entity.UserEntity;
import com.gnsbooking.app.repository.UserRepository;
import com.gnsbooking.app.serviceI.UserServiceI;

@Service
public class UserServiceImpl implements UserServiceI {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	EmailService emailService;

	@Override
	public void sendOtp(String email) {
		String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

		// Save OTP in Redis (5 min expiry)
		 redisTemplate.opsForValue().set("OTP:" + email, otp, Duration.ofMinutes(5));
		
		// Send email
		emailService.sendOtpEmail(email, otp);
	}

	@Override
	public UserEntity verifyOtp(VerifyOtpRequest request) {
		String key = "OTP:" + request.getEmail();
        String storedOtp = (String) redisTemplate.opsForValue().get(key);

        // ❌ OTP expired
        if (storedOtp == null) {
            throw new RuntimeException("OTP expired");
        }

        // ❌ Wrong OTP
        if (!storedOtp.equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        // ✅ OTP correct → remove OTP
        redisTemplate.delete(key);

        // 🔥 CHECK USER EXISTS OR NOT
        Optional<UserEntity> optionalUser =
                userRepository.findByEmailIgnoreCase(request.getEmail());

        if (optionalUser.isPresent()) {
            // ✅ Existing user → return directly
            return optionalUser.get();
        } else {
            // ❌ New user → create and save
            UserEntity user = new UserEntity();
            user.setEmail(request.getEmail());
            user.setAdmin(false);

            return userRepository.save(user);
        }
	}

}
