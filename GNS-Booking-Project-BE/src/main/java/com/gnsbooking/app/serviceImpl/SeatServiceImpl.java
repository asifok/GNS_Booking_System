package com.gnsbooking.app.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.gnsbooking.app.dto.SeatResponse;
import com.gnsbooking.app.entity.SeatEntity;
import com.gnsbooking.app.repository.SeatRepository;
import com.gnsbooking.app.serviceI.SeatServiceI;

@Service
public class SeatServiceImpl implements SeatServiceI {
	
	@Autowired
	SeatRepository seatRepository;
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public List<SeatResponse> getAllSeats() {

	    List<SeatEntity> seats = seatRepository.findAll();
	    if (seats == null || seats.isEmpty()) {
	        return List.of();
	    }

	    // Step 1: Prepare all Redis keys
	    List<String> keys = seats.stream()
	            .map(seat -> "seat:" + seat.getSeatNumber())
	            .toList();

	    // Step 2: Bulk fetch from Redis
	    List<Object> redisValues = redisTemplate.opsForValue().multiGet(keys);

	    // Step 3: Convert to Set for fast lookup
	    Set<String> lockedSeats = new HashSet<>();
	    for (int i = 0; i < keys.size(); i++) {
	        if (redisValues.get(i) != null) {
	            lockedSeats.add(keys.get(i));
	        }
	    }

	    // Step 4: Map response
	    return seats.stream().map(seat -> {
	        String key = "seat:" + seat.getSeatNumber();
	        String finalStatus = lockedSeats.contains(key) ? "LOCKED" : seat.getStatus();

	        return new SeatResponse(
	                seat.getSeatNumber(),
	                seat.getRowName(),
	                seat.getSection(),
	                seat.getPrice(),
	                finalStatus
	        );
	    }).toList();
	}

}
