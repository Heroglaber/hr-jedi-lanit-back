package ru.lanit.bpm.jedu.hrjedi.app;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.lanit.bpm.jedu.hrjedi.app.api.employee.EmployeeService;
import ru.lanit.bpm.jedu.hrjedi.domain.Employee;
import ru.lanit.bpm.jedu.hrjedi.domain.security.RoleCount;
import ru.lanit.bpm.jedu.hrjedi.fw.security.WebSecurityConfig;
import ru.lanit.bpm.jedu.hrjedi.fw.spring.HrJediApplication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HrJediApplication.class, WebSecurityConfig.class})
public class EmployeeServiceIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void shouldReturnNumberOfAdmins() {
        assertThat(employeeService.getNumberOfAdmins()).isGreaterThan(0);
    }

    @Test
    public void shouldReturnAllEmployeesWithRoles() {
        List<Employee> employees = employeeService.getAllWithRoles();
        assertThat(employees).allMatch(employee -> !employee.getRoles().isEmpty());
    }

    @Test
    public void souldReturnNumberOfEmployeesByRoles() {
        List<RoleCount> roles = employeeService.getNumberOfEmployeesByRole();
        for(RoleCount count : roles) {
            assertThat(count.getTotal()).isGreaterThan(0);
        }
    }
}
