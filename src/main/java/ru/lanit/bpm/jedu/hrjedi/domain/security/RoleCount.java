package ru.lanit.bpm.jedu.hrjedi.domain.security;

public class RoleCount {
    private RoleName name;
    private Long total;

    public RoleCount(RoleName name, Long total) {
        this.name = name;
        this.total = total;
    }

    public RoleName getName() {
        return name;
    }

    public Long getTotal() {
        return total;
    }
}
