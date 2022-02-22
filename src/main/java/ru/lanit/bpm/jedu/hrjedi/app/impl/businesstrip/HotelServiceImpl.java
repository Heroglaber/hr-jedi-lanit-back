package ru.lanit.bpm.jedu.hrjedi.app.impl.businesstrip;

import org.springframework.stereotype.Service;
import ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.businessTrip.HotelRepository;
import ru.lanit.bpm.jedu.hrjedi.app.api.businessTrip.HotelService;
import ru.lanit.bpm.jedu.hrjedi.domain.Hotel;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {
    private HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public List<Hotel> getAll() {
        return hotelRepository.findAll();
    }
}
