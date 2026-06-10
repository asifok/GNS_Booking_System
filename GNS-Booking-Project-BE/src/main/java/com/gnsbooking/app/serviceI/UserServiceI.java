package com.gnsbooking.app.serviceI;

import com.gnsbooking.app.dto.VerifyOtpRequest;
import com.gnsbooking.app.entity.UserEntity;

public interface UserServiceI {

	void sendOtp(String email);

	UserEntity verifyOtp(VerifyOtpRequest request);

}
