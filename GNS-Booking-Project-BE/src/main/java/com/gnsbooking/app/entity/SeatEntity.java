package com.gnsbooking.app.entity;

import jakarta.persistence.*;

@Entity
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @Column(unique = true)
    private String seatNumber;

    private String rowName;

    private String section;   // LEFT / CENTER / RIGHT / BLOCKED

    private Integer price;

    private String status;    // AVAILABLE / BOOKED

    // 🔹 Default Constructor
    public SeatEntity() {
    }

    // 🔹 Parameterized Constructor
    public SeatEntity(Long seatId, String seatNumber, String rowName,
                      String section, Integer price, String status) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.rowName = rowName;
        this.section = section;
        this.price = price;
        this.status = status;
    }

    // 🔹 Getters & Setters

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}