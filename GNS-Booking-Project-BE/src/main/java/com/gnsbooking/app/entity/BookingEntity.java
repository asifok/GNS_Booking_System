package com.gnsbooking.app.entity;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class BookingEntity {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)	
	 private Long bkngId;
	 private int totalAmount;
	 private String status;  // CONFIRMED / PENDING
	 
	 // 🔥 Many bookings → one user
	 @ManyToOne
	 @JoinColumn(name = "user_id")
	 private UserEntity user;
	 
	 private boolean isAprvd=false;
	 private String pymntMthd;

	 // 🔥 One booking → many seats
	 @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
	 private List<BookingSeatEntity> seats;

	 // getters & setters
	 public Long getBkngId() { return bkngId; }
	 public void setBkngId(Long bkngId) { this.bkngId = bkngId; }
	 public int getTotalAmount() { return totalAmount; }
	 public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
	 public String getStatus() { return status; }
	 public void setStatus(String status) { this.status = status; }
	 public UserEntity getUser() { return user; }
	 public void setUser(UserEntity user) { this.user = user; }
	 public List<BookingSeatEntity> getSeats() { return seats; }
	 public void setSeats(List<BookingSeatEntity> seats) { this.seats = seats; }
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
	 
}