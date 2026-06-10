package com.gnsbooking.app.serviceI;

import java.util.List;

import com.gnsbooking.app.dto.AllTicketResponse;
import com.gnsbooking.app.dto.BookingRequest;
import com.gnsbooking.app.dto.CancelSeatsRequest;
import com.gnsbooking.app.dto.ConfirmBookingRequest;
import com.gnsbooking.app.dto.TicketResponse;
import com.gnsbooking.app.entity.BookingEntity;

public interface BookingServiceI {

	BookingEntity createBooking(BookingRequest request);

	List<TicketResponse> getUserTickets(String email);

	List<AllTicketResponse> getAllTickets(String email);

	BookingEntity confirmBooking(ConfirmBookingRequest request);

	String cancelSeats(CancelSeatsRequest request);

}
