package com.gnsbooking.app.dto;


public class ConfirmBookingRequest {

    private Long bkngId;
    private boolean isAprvd;
    private String pymntMthd;
    private String email;

    public Long getBkngId() {
		return bkngId;
	}
	public void setBkngId(Long bkngId) {
		this.bkngId = bkngId;
	}
	public boolean isAprvd() {
		return isAprvd;
	}
	public void setAprvd(boolean isAprvd) {
		this.isAprvd = isAprvd;
	}
	public String getPymntMthd() {
		return pymntMthd;
	}
	public void setPymntMthd(String pymntMthd) {
		this.pymntMthd = pymntMthd;
	}
	public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}