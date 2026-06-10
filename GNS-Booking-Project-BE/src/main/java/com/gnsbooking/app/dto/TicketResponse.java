package com.gnsbooking.app.dto;

import java.util.List;

public class TicketResponse {

    private Long bookingId;
    private int totalAmount;
    private String status;
    private List<String> seats;

    public TicketResponse(Long bookingId, int totalAmount,
                          String status, List<String> seats) {
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.seats = seats;
    }

    // getters
    public Long getBookingId() { return bookingId; }
    public int getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public List<String> getSeats() { return seats; }
}
