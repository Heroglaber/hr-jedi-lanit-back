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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NaturalId;
import ru.lanit.bpm.jedu.hrjedi.model.security.Role;
import ru.lanit.bpm.jedu.hrjedi.model.security.State;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "EMPLOYEE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "LOGIN"
    })
})
public class Employee implements Serializable {
    private static final long serialVersionUID = -5449326074498337967L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_id_generator")
    @SequenceGenerator(name = "employee_id_generator", sequenceName = "sq_employee_id", allocationSize = 1)
    private Long id;

    @NaturalId
    private String login;

    private String firstName;
    private String secondName;
    private String lastName;

    private String hashPassword;

    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "EMPLOYEE_ROLE",
        joinColumns = @JoinColumn(name = "EMPLOYEE_ID"),
        inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<Role> roles = new HashSet<>();

    @Enumerated(value = EnumType.STRING)
    private State state;

    public Employee() {
    }

    public Employee(String login, String firstName, String secondName, String lastName, String hashPassword, String email) {
        this.login = login;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.hashPassword = hashPassword;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    @JsonIgnore
    public String getFullName() {
        return secondName == null ? String.format("%s %s", firstName, lastName) : String.format("%s %s %s", firstName, secondName, lastName);
    }
}
