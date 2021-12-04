package ru.lanit.bpm.jedu.hrjedi.adapter.restservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lanit.bpm.jedu.hrjedi.app.vacation.VacationService;

import java.util.Set;

@RestController
@RequestMapping("/hr-rest/vacations")
public class VacationController {
    private final VacationService vacationService;

    public VacationController(VacationService vacationService) {
        this.vacationService = vacationService;
    }

    @GetMapping("/approve/{approver}")
    public Set<String> vacationsToApprove(@PathVariable("approver") String approver) {
        return vacationService.findVacationsToApprove(approver);
    }
}
