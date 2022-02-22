package ru.lanit.bpm.jedu.hrjedi.adapter.restservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lanit.bpm.jedu.hrjedi.app.api.businessTrip.HotelService;
import ru.lanit.bpm.jedu.hrjedi.domain.Hotel;

import java.util.List;

@RestController
@RequestMapping("/hr-rest/hotels")
public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('OMNI') or hasRole('ADMIN') or hasRole('HR')")
    public List<Hotel> getAll() {
        return hotelService.getAll();
    }
}
