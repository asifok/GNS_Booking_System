package com.gnsbooking.app.dto;

import java.util.List;

public class AllTicketResponse {

    private Long bookingId;
    private String email;
    private int totalAmount;
    private String status;
    private List<String> seats;
    private String paymentMethod;
    private Boolean isApproved;

    public AllTicketResponse(Long bookingId, String email, int totalAmount,
                             String status, List<String> seats,
                             String paymentMethod, Boolean isApproved) {
        this.bookingId = bookingId;
        this.email = email;
        this.totalAmount = totalAmount;
        this.status = status;
        this.seats = seats;
        this.paymentMethod = paymentMethod;
        this.isApproved = isApproved;
    }

    public Long getBookingId() { return bookingId; }
    public String getEmail() { return email; }
    public int getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public List<String> getSeats() { return seats; }
    public String getPaymentMethod() { return paymentMethod; }
    public Boolean getIsApproved() { return isApproved; }
}
