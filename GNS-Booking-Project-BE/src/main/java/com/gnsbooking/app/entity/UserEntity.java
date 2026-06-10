package com.gnsbooking.app.entity;

import java.util.List;
import jakarta.persistence.*;

@Entity
public class UserEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(unique = true, nullable = false)
	private String email;

	private String name;
	private String gender;
	private String mobileNumber;
	    
	private boolean isAdmin = false;

	// 🔥 One user → many bookings
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<BookingEntity> bookings;

	// getters & setters
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getGender() { return gender; }
	public void setGender(String gender) { this.gender = gender; }
	public String getMobileNumber() { return mobileNumber; }
	public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean admin) { isAdmin = admin; }
	public List<BookingEntity> getBookings() { return bookings; }
	public void setBookings(List<BookingEntity> bookings) { this.bookings = bookings; }
}