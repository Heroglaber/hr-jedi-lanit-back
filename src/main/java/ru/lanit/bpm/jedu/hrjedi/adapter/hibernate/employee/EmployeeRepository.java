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
package ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.lanit.bpm.jedu.hrjedi.domain.Employee;
import ru.lanit.bpm.jedu.hrjedi.domain.security.RoleCount;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByLoginIgnoreCase(String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    @Query("select e from Employee e join fetch e.roles r")
    List<Employee> findAllWithRoles();

    @Query("select new ru.lanit.bpm.jedu.hrjedi.domain.security.RoleCount(r.name, count(r.name))" +
        " from Employee e join e.roles r group by r.name")
    List<RoleCount> countEmployeesByRole();
}
