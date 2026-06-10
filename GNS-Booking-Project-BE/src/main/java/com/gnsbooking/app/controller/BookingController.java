package com.gnsbooking.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gnsbooking.app.dto.AllTicketResponse;
import com.gnsbooking.app.dto.BookingRequest;
import com.gnsbooking.app.dto.CancelSeatsRequest;
import com.gnsbooking.app.dto.ConfirmBookingRequest;
import com.gnsbooking.app.dto.TicketResponse;
import com.gnsbooking.app.entity.BookingEntity;
import com.gnsbooking.app.serviceI.BookingServiceI;


@RestController
@RequestMapping("/booking")
@CrossOrigin("*")   // ✅ Allow all origins
public class BookingController {
	
	@Autowired
	BookingServiceI bookingServiceI;
	
	@PostMapping("/save")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
        	System.out.println("Controller Seat Numbers : "+request.getSeatNmbrs());
        	System.out.println("Controller Email : "+request.getEmail());
            BookingEntity booking = bookingServiceI.createBooking(request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
	@GetMapping("/tickets/all/get")
    public ResponseEntity<?> getUserTickets(@RequestParam String email) {

        try {
            List<TicketResponse> tickets = bookingServiceI.getUserTickets(email);

            return ResponseEntity.ok(tickets);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
	 @GetMapping("/tickets/admin/all/get")
	 public ResponseEntity<?> getAllTickets(@RequestParam String email) {
		 try {
			 List<AllTicketResponse> response =	bookingServiceI.getAllTickets(email);
			 return ResponseEntity.ok(response);
		 } catch (Exception e) {
			 return ResponseEntity.badRequest().body(e.getMessage());
		 }
	 }
	 
	 @PostMapping("/confirm")
	 public ResponseEntity<?> confirmBooking(@RequestBody ConfirmBookingRequest request) {

	     try {
	         BookingEntity booking = bookingServiceI.confirmBooking(request);

	         return ResponseEntity.ok(booking);

	     } catch (Exception e) {
	         return ResponseEntity.badRequest().body(e.getMessage());
	     }
	 }
	 
	 @PostMapping("/cancel-seats")
	 public ResponseEntity<?> cancelSeats(@RequestBody CancelSeatsRequest request) {	
		 try {
			 String response = bookingServiceI.cancelSeats(request);
			 return ResponseEntity.ok(response);
		 } catch (Exception e) {
			 return ResponseEntity.badRequest().body(e.getMessage());
		 }
	 }
}