package com.gnsbooking.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gnsbooking.app.dto.SeatResponse;
import com.gnsbooking.app.serviceI.SeatServiceI;

@RestController
@RequestMapping("/seat")
@CrossOrigin("*")   // ✅ Allow all origins
public class SeatController {
	
	@Autowired
	SeatServiceI seatServiceI;
	
	@GetMapping("/all/get")
	public ResponseEntity<?> getAllSeats() {
		List<SeatResponse> seats = seatServiceI.getAllSeats();
		return ResponseEntity.ok(seats);
	}
	
}
