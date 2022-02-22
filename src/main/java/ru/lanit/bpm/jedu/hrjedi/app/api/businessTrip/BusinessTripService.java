package ru.lanit.bpm.jedu.hrjedi.app.api.businessTrip;

import ru.lanit.bpm.jedu.hrjedi.domain.BusinessTrip;

import java.util.Set;

public interface BusinessTripService {
    Set<String> findBusinessTripToApprove(String approverLogin);

    void saveBusinessTrip(BusinessTrip businessTrip);
}
