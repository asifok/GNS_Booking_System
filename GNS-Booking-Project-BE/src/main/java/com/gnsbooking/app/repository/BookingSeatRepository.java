package com.gnsbooking.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gnsbooking.app.entity.BookingSeatEntity;
import com.gnsbooking.app.entity.UserEntity;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeatEntity, Long>{

	boolean existsBySeatNmbr(String seat);

	Optional<UserEntity> findBySeatNmbr(String seat);

}
