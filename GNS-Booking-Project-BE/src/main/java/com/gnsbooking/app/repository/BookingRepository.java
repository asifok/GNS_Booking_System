package com.gnsbooking.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gnsbooking.app.entity.BookingEntity;
import com.gnsbooking.app.entity.UserEntity;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

	List<BookingEntity> findByUser(Optional<UserEntity> user);

	List<BookingEntity> findAllByOrderByBkngIdDesc();

}
