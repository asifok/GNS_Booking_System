package com.gnsbooking.app.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.gnsbooking.app.dto.AllTicketResponse;
import com.gnsbooking.app.dto.BookingRequest;
import com.gnsbooking.app.dto.CancelSeatsRequest;
import com.gnsbooking.app.dto.ConfirmBookingRequest;
import com.gnsbooking.app.dto.TicketResponse;
import com.gnsbooking.app.entity.BookingEntity;
import com.gnsbooking.app.entity.BookingSeatEntity;
import com.gnsbooking.app.entity.SeatEntity;
import com.gnsbooking.app.entity.UserEntity;
import com.gnsbooking.app.repository.BookingRepository;
import com.gnsbooking.app.repository.BookingSeatRepository;
import com.gnsbooking.app.repository.SeatRepository;
import com.gnsbooking.app.repository.UserRepository;
import com.gnsbooking.app.serviceI.BookingServiceI;

import jakarta.transaction.Transactional;

@Service
public class BookingServiceImpl implements BookingServiceI {

    private final EmailService emailService;
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	BookingRepository bookingRepository;
	
	@Autowired
	BookingSeatRepository bookingSeatRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SeatRepository seatRepository;

    BookingServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

	@Override
    @Transactional
    public BookingEntity createBooking(BookingRequest request) {

		String email = request.getEmail();
		
		System.out.println("Email : "+email);

		if (email == null || email.trim().isEmpty()) {
		    throw new RuntimeException("Email is required");
		}

		email = email.trim();
		
		Optional<UserEntity> user =
		        userRepository.findByEmailIgnoreCase(email);        
        System.out.println("Line 50 :"+user.isPresent());
        
        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }

        if (request.getSeatNmbrs() == null || request.getSeatNmbrs().isEmpty()) {
            throw new RuntimeException("Seat list cannot be empty");
        }
    
        // 🔥 NEW RULE: MAX 5 SEATS
        if (request.getSeatNmbrs().size() > 5) {
            throw new RuntimeException("You can book maximum 5 seats at a time");
        }

        // 🔥 STEP 2: VALIDATE FIRST
        for (String seat : request.getSeatNmbrs()) {
        	// ❌ Already booked
            if (bookingSeatRepository.existsBySeatNmbr(seat)) {
                throw new RuntimeException("Seat already booked: " + seat);
            }

            // ❌ Already locked
            if (redisTemplate.hasKey("seat:" + seat)) {
                throw new RuntimeException("Seat already locked: " + seat);
            }

            // ❌ Seat not exist
            if (seatRepository.findBySeatNumber(seat) == null) {
                throw new RuntimeException("Seat not found: " + seat);
            }
        }

        // 🔒 STEP 3: LOCK ALL SEATS
        for (String seat : request.getSeatNmbrs()) {
            redisTemplate.opsForValue()
                    .set("seat:" + seat, user.get().getUserId(), 5, TimeUnit.MINUTES);
        }

        int total = 0;
        List<BookingSeatEntity> bookingSeats = new ArrayList<>();
        List<SeatEntity> seatsToUpdate = new ArrayList<>();

        try {
        	// 🎯 STEP 4: CALCULATE PRICE
            for (String seatNo : request.getSeatNmbrs()) {
            	SeatEntity seat = seatRepository.findBySeatNumber(seatNo);

                total += seat.getPrice();
                
                // 🔥 UPDATE STATUS TO BOOKED
                seat.setStatus("BOOKED");
                seatsToUpdate.add(seat);

                BookingSeatEntity bs = new BookingSeatEntity();
                bs.setSeatNmbr(seatNo);
                bs.setPrice(seat.getPrice());

                bookingSeats.add(bs);
            }

            // 💾 STEP 5: SAVE BOOKING
            BookingEntity booking = new BookingEntity();
            booking.setUser(user.get());
            booking.setTotalAmount(total);
            booking.setStatus("PAYMENT_PENDING");

            bookingRepository.save(booking);

            // 💾 STEP 6: SAVE BOOKING SEATS
            for (BookingSeatEntity bs : bookingSeats) {
                bs.setBooking(booking);
                bookingSeatRepository.save(bs);
            }
            	
            seatRepository.saveAll(seatsToUpdate);
           
            // 🔓 STEP 7: UNLOCK SEATS
            for (String seat : request.getSeatNmbrs()) {
                redisTemplate.delete("seat:" + seat);
            }

            emailService.sendBookingConfirm(request.getEmail(), request.getSeatNmbrs(), booking.getBkngId(), total);
            return booking;

        } catch (Exception e) {

            // ❗ ROLLBACK LOCKS
            for (String seat : request.getSeatNmbrs()) {
                redisTemplate.delete("seat:" + seat);
            }

            throw new RuntimeException("Booking failed: " + e.getMessage());
        }
    }
	
	@Override
	public List<TicketResponse> getUserTickets(String email) {

	    if (email == null || email.trim().isEmpty()) {
	        throw new RuntimeException("Email is required");
	    }

	    email = email.trim();

	    Optional<UserEntity> user = userRepository.findByEmailIgnoreCase(email);

	    if (!user.isPresent()) {
	        throw new RuntimeException("User not found");
	    }

	    List<BookingEntity> bookings = bookingRepository.findByUser(user);

	    if (bookings == null || bookings.isEmpty()) {
	        return List.of(); // ✅ return empty list
	    }

	    List<TicketResponse> response = new ArrayList<>();

	    for (BookingEntity booking : bookings) {

	        List<String> seatList = new ArrayList<>();

	        for (BookingSeatEntity seat : booking.getSeats()) {
	            seatList.add(seat.getSeatNmbr());
	        }

	        response.add(new TicketResponse(
	                booking.getBkngId(),
	                booking.getTotalAmount(),
	                booking.getStatus(),
	                seatList
	        ));
	    }

	    return response;
	}
	
	@Override
	public List<AllTicketResponse> getAllTickets(String email) {

	    // 🔥 STEP 1: VALIDATE EMAIL
	    if (email == null || email.trim().isEmpty()) {
	        throw new RuntimeException("Email is required");
	    }

	    email = email.trim();

	    // 🔥 STEP 2: FETCH USER
	    UserEntity user = userRepository.findByEmailIgnoreCase(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    // 🔥 STEP 3: ADMIN CHECK
	    if (!user.isAdmin()) {
	        throw new RuntimeException("Access denied: Admin only");
	    }

	    // 🔥 STEP 4: FETCH BOOKINGS
	    List<BookingEntity> bookings = bookingRepository.findAllByOrderByBkngIdDesc();

	    if (bookings == null || bookings.isEmpty()) {
	        return List.of();
	    }

	    List<AllTicketResponse> response = new ArrayList<>();

	    for (BookingEntity booking : bookings) {

	        List<String> seatList = new ArrayList<>();

	        for (BookingSeatEntity seat : booking.getSeats()) {
	            seatList.add(seat.getSeatNmbr());
	        }

	        response.add(new AllTicketResponse(
	                booking.getBkngId(),
	                booking.getUser().getEmail(),
	                booking.getTotalAmount(),
	                booking.getStatus(),
	                seatList,
	                booking.getPymntMthd(),
	                booking.isAprvd()
	        ));
	    }

	    return response;
	}
	
	@Override
	@Transactional
	public BookingEntity confirmBooking(ConfirmBookingRequest request) {

	    // 🔥 STEP 1: VALIDATE EMAIL
	    if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
	        throw new RuntimeException("Admin email required");
	    }

	    String email = request.getEmail().trim();

	    // 🔥 STEP 2: CHECK ADMIN
	    UserEntity admin = userRepository.findByEmailIgnoreCase(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    if (!admin.isAdmin()) {
	        throw new RuntimeException("Access denied: Admin only");
	    }

	    // 🔥 STEP 3: FETCH BOOKING
	    BookingEntity booking = bookingRepository.findById(request.getBkngId())
	            .orElseThrow(() -> new RuntimeException("Booking not found"));

	    // 🔥 STEP 4: APPLY CONDITION
	    if (Boolean.TRUE.equals(request.isAprvd())) {

	        // ✅ APPROVED → mark as PAID
	        booking.setStatus("PAID");

	    } else {

	        // ❌ NOT APPROVED → keep pending
	        booking.setStatus("PAYMENT_PENDING");
	    }

	    // 🔥 ALWAYS update these
	    booking.setPymntMthd(request.getPymntMthd());
	    booking.setAprvd(request.isAprvd());

	    bookingRepository.save(booking);
	    
	    if (Boolean.TRUE.equals(request.isAprvd())) {
	    	 // 🔥 Extract seat list
	        List<String> seats = booking.getSeats()
	                .stream()
	                .map(BookingSeatEntity::getSeatNmbr)
	                .toList();
	        emailService.sendTickets(booking.getBkngId(), seats, booking.getTotalAmount(), booking.getUser().getEmail());
	    		
	    }
	    return booking;
	}

	 @Override
	 @Transactional
	 public String cancelSeats(CancelSeatsRequest request) {
		 // 🔥 STEP 1: VALIDATE EMAIL
		 if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
			 throw new RuntimeException("Admin email required");
		 }
		 String email = request.getEmail().trim();

		 // 🔥 STEP 2: CHECK ADMIN
		 UserEntity admin = userRepository.findByEmailIgnoreCase(email)
				 .orElseThrow(() -> new RuntimeException("User not found"));
		 if (!admin.isAdmin()) {
			 throw new RuntimeException("Access denied: Admin only");
		 }
		 
		 // 🔥 STEP 3: FETCH BOOKING
		 BookingEntity booking = bookingRepository.findById(request.getBkngId())
				 .orElseThrow(() -> new RuntimeException("Booking not found"));
		 
		 List<String> seatsToCancel = request.getSeatNmbrs();
		 if (seatsToCancel == null || seatsToCancel.isEmpty()) {
			 throw new RuntimeException("Seat list cannot be empty");
		 }
		 int refundAmount = 0;
		 for (String seatNo : seatsToCancel) {
			 // 🔥 FIND SEAT IN BOOKING
			 Optional<BookingSeatEntity> bsOpt =
					 booking.getSeats().stream()
					 .filter(s -> s.getSeatNmbr().equals(seatNo))
					 .findFirst();
			 if (!bsOpt.isPresent()) {
				 throw new RuntimeException("Seat not found in booking: " + seatNo);
			 }
			 BookingSeatEntity bs = bsOpt.get();
			 refundAmount += bs.getPrice();
			 
			 // 🔥 REMOVE FROM LIST (IMPORTANT)
			 booking.getSeats().remove(bs);

			 // 🔥 DELETE FROM DB
			 bookingSeatRepository.delete(bs);

			 // 🔥 UPDATE SEAT STATUS → AVAILABLE
			 SeatEntity seat = seatRepository.findBySeatNumber(seatNo);
			 seat.setStatus("AVAILABLE");
			 seatRepository.save(seat);
		 }
		 
		 // 🔥 UPDATE BOOKING TOTAL
		 booking.setTotalAmount(booking.getTotalAmount() - refundAmount);
		 bookingRepository.save(booking);
		 emailService.sendBookingCancelToUser(booking,refundAmount,request.getSeatNmbrs());
		 emailService.sendBookingCancelToAdmin(booking,refundAmount,request.getSeatNmbrs());
		 return "Seats cancelled successfully. Refund amount: " + refundAmount;
	 }
}