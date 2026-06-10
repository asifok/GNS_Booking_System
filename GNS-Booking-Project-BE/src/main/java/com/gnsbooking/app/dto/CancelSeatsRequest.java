package com.gnsbooking.app.dto;


import java.util.List;

public class CancelSeatsRequest {

    private String email;
    private Long bkngId;
    private List<String> seatNmbrs;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
	public Long getBkngId() {
		return bkngId;
	}
	public void setBkngId(Long bkngId) {
		this.bkngId = bkngId;
	}
	public List<String> getSeatNmbrs() {
		return seatNmbrs;
	}
	public void setSeatNmbrs(List<String> seatNmbrs) {
		this.seatNmbrs = seatNmbrs;
	}

}
