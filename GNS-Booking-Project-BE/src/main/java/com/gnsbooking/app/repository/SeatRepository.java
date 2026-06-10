package com.gnsbooking.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gnsbooking.app.entity.SeatEntity;
import com.gnsbooking.app.entity.UserEntity;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long>{

	SeatEntity findBySeatNumber(String seatNo);

}
