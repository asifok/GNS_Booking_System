package com.gnsbooking.app.dto;

import java.util.List;

public class BookingRequest {

    private String email;
    private List<String> seatNmbrs;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getSeatNmbrs() {
		return seatNmbrs;
	}
	public void setSeatNmbrs(List<String> seatNmbrs) {
		this.seatNmbrs = seatNmbrs;
	}
}