package com.gnsbooking.app.dto;

public class SeatResponse {

    private String seatNumber;
    private String rowName;
    private String section;
    private Integer price;
    private String status;

    public SeatResponse(String seatNumber, String rowName,
                        String section, Integer price, String status) {
        this.seatNumber = seatNumber;
        this.rowName = rowName;
        this.section = section;
        this.price = price;
        this.status = status;
    }

    // getters
    public String getSeatNumber() { return seatNumber; }
    public String getRowName() { return rowName; }
    public String getSection() { return section; }
    public Integer getPrice() { return price; }
    public String getStatus() { return status; }
}
