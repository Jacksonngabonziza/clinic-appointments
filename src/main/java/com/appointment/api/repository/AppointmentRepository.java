package com.appointment.api.repository;

import com.appointment.api.model.appointment;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<appointment, Long> {
    
   @Query("SELECT p FROM appointment p WHERE p.id=?1")
   appointment findappById(Long id);
   @Query("SELECT p FROM appointment p WHERE p.phoneNumber=?1")
   appointment findAppointmentByPhone(String phoneNumber);

   @Transactional
   @Modifying
   @Query("update appointment p set p.status = ?1 where p.id = ?2")
   void approve(String status, Long id);

   @Transactional
   @Modifying
   @Query("SELECT  schedule_time from appointment")
   List<Date> booked();


   @Query("SELECT p FROM appointment p WHERE p.schedule_time=?1")
    appointment findAppointmentByDate(Date d1);


}
