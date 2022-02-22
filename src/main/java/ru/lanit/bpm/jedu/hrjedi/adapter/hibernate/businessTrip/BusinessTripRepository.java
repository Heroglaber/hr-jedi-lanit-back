package ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.businessTrip;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lanit.bpm.jedu.hrjedi.domain.BusinessTrip;

public interface BusinessTripRepository extends JpaRepository<BusinessTrip, Long> {
}
