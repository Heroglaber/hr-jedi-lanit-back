/*
 * Copyright (c) 2008-2020
 * LANIT
 * All rights reserved.
 *
 * This product and related documentation are protected by copyright and
 * distributed under licenses restricting its use, copying, distribution, and
 * decompilation. No part of this product or related documentation may be
 * reproduced in any form by any means without prior written authorization of
 * LANIT and its licensors, if any.
 *
 * $
 */
package ru.lanit.bpm.jedu.hrjedi.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "VACATION")
public class Vacation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vacation_id_generator")
    @SequenceGenerator(name = "vacation_id_generator", sequenceName = "sq_vacation_id", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    private LocalDate start;
    private LocalDate end;

    public Vacation() {
    }

    public Vacation(Employee employee, LocalDate start, LocalDate end) {
        this.employee = employee;
        this.start = start;
        this.end = end;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }
}
