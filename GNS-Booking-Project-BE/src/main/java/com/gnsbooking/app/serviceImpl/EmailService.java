package com.gnsbooking.app.serviceImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gnsbooking.app.entity.BookingEntity;
import com.gnsbooking.app.utility.EmailTemplate;

@Service
public class EmailService {

	@Autowired
	EmailTemplate emailTemplate;
	
    @Value("${brevo.api.key}")
    private String apiKey;

    private static final String URL = "https://api.brevo.com/v3/smtp/email";

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> body = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", "GNS Booking");
            sender.put("email", "ticket4naach@gmail.com");

            Map<String, String> to = new HashMap<>();
            to.put("email", toEmail);
            body.put("sender", sender);
            body.put("to", Collections.singletonList(to));
            body.put("subject", "GNS Booking - Your OTP Verification Code");
            body.put("htmlContent", emailTemplate.getOtpTemplate(otp));

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
			headers.set("accept", "application/json");
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(URL, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
    
    @Async
    public void sendBookingConfirm(String email, List<String> seatNmbrs, Long bkngId, int total) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> body = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", "GNS Booking");
            sender.put("email", "ticket4naach@gmail.com");

            Map<String, String> to = new HashMap<>();
            to.put("email", email);
            body.put("sender", sender);
            body.put("to", Collections.singletonList(to));
            body.put("subject", "🎉 Your Booking is Created - GNS Event");
            body.put("htmlContent", emailTemplate.bookingConfirmTemplate(seatNmbrs, bkngId, total));

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
			headers.set("accept", "application/json");
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(URL, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
    
    @Async
	public void sendTickets(Long bkngId, List<String> seats, int totalAmount, String email) {
    	try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> body = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", "GNS Booking");
            sender.put("email", "ticket4naach@gmail.com");

            Map<String, String> to = new HashMap<>();
            to.put("email", email);
            body.put("sender", sender);
            body.put("to", Collections.singletonList(to));
            body.put("subject", "🎟️ Your Tickets Confirmed - NAACH '26");
            body.put("htmlContent", emailTemplate.getApprovedTicketTemplate(bkngId, seats, totalAmount));

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
			headers.set("accept", "application/json");
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(URL, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
    
    @Async
	public void sendBookingCancelToUser(BookingEntity booking, int refundAmount, List<String> seatNmbrs) {
    	try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> body = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", "GNS Booking");
            sender.put("email", "ticket4naach@gmail.com");

            Map<String, String> to = new HashMap<>();
            to.put("email", booking.getUser().getEmail());
            body.put("sender", sender);
            body.put("to", Collections.singletonList(to));
            body.put("subject", "Booking Update: Seats Cancelled & Refund Initiated (ID: " + booking.getBkngId() + ")");
            body.put("htmlContent", emailTemplate.cancelTicketToUserTemplate(booking.getBkngId(), seatNmbrs, refundAmount));

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(URL, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
	}
    
    @Async
	public void sendBookingCancelToAdmin(BookingEntity booking, int refundAmount, List<String> seatNmbrs) {
    	try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> body = new HashMap<>();

            Map<String, String> sender = new HashMap<>();
            sender.put("name", "GNS Booking");
            sender.put("email", "ticket4naach@gmail.com");

            Map<String, String> to = new HashMap<>();
            to.put("email", "gurudevnatrajstudio@gmail.com");
            body.put("sender", sender);
            body.put("to", Collections.singletonList(to));
            body.put("subject", "Admin Alert: Seats Cancelled as per User Request (Booking ID: " + booking.getBkngId() + ")");
            body.put("htmlContent", emailTemplate.cancelTicketToAdminTemplate(booking.getBkngId(), seatNmbrs, refundAmount, booking.getUser().getEmail()));

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(URL, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }		
	}
}
