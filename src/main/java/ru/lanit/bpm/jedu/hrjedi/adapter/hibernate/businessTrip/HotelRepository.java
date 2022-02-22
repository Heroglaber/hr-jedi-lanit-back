package ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.businessTrip;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanit.bpm.jedu.hrjedi.domain.Hotel;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Optional<Hotel> findByName(String name);
    List<Hotel> findAll();
}
