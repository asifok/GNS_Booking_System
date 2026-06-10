package com.gnsbooking.app.entity;

import jakarta.persistence.*;

@Entity
public class BookingSeatEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bkngSeatId;
	private String seatNmbr;
	private int price;
	
	// 🔥 Many seats → one booking
	@ManyToOne
	@JoinColumn(name = "bkng_id")
	private BookingEntity booking;

	public Long getBkngSeatId() {
		return bkngSeatId;
	}

	public void setBkngSeatId(Long bkngSeatId) {
		this.bkngSeatId = bkngSeatId;
	}

	public String getSeatNmbr() {
		return seatNmbr;
	}

	public void setSeatNmbr(String seatNmbr) {
		this.seatNmbr = seatNmbr;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public BookingEntity getBooking() {
		return booking;
	}

	public void setBooking(BookingEntity booking) {
		this.booking = booking;
	}
}