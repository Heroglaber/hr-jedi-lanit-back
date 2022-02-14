package ru.lanit.bpm.jedu.hrjedi.adapter.restservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EmployeeDto {
    private String login;
    private String firstName;
    private String patronymic;
    private String lastName;
    private Set<String> roles;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmployeeDto that = (EmployeeDto) o;
        return login.equals(that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    public String getLogin() {
        return login;
    }

    @JsonProperty("username")
    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("first-name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    @JsonProperty("second-name")
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getLastName() {
        return lastName;
    }

    @JsonProperty("last-name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<String> getRoles() {
        return roles;
    }

    @JsonProperty("roles")
    public void setRoles(String[] roles) {
        this.roles = new HashSet<>(Arrays.asList(roles));
    }

    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }
}
