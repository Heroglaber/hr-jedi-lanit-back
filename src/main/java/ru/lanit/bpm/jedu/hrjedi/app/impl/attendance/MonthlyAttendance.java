package ru.lanit.bpm.jedu.hrjedi.app.impl.attendance;

import ru.lanit.bpm.jedu.hrjedi.domain.Attendance;

import java.time.Duration;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AttendanceReportRow represents a row for the attendance report:
 * employee name | attendance totalHours for specified month | employee's office
 */
public class MonthlyAttendance implements Comparator<MonthlyAttendance> {
    private String employeeFullName;
    private YearMonth yearMonth;
    private long totalHours;
    private String officeName;

    public MonthlyAttendance(YearMonth yearMonth, Attendance attendance) {
        this(yearMonth, attendance.getEmployee().getFullName(), attendance.getOffice().getName());
    }

    public MonthlyAttendance(YearMonth yearMonth, String employeeFullName, String officeName) {
        this.yearMonth = yearMonth;
        this.employeeFullName = employeeFullName;
        this.officeName = officeName;
    }

    public MonthlyAttendance() {
    }

    public static List<MonthlyAttendance> summarizeAttendancePerMonth(YearMonth yearMonth, List<Attendance> attendanceList) {
        double secondsInHour = 3600.0;
        Map<MonthlyAttendance, LongSummaryStatistics> monthlyAttendanceMap = attendanceList.stream()
            .collect(Collectors.groupingBy(attendance -> new MonthlyAttendance(yearMonth, attendance),
                Collectors.summarizingLong(attendance ->
                    Duration.between(attendance.getEntranceTime(), attendance.getExitTime()).getSeconds()
                )));
        return monthlyAttendanceMap.entrySet().stream()
            .map(e -> {
                e.getKey().setTotalHours((int) Math.ceil(e.getValue().getSum() / secondsInHour));
                return e.getKey();
            }).collect(Collectors.toList());
    }

    @Override
    public int compare(MonthlyAttendance o1, MonthlyAttendance o2) {
        int officeNameCompareResult = OfficeOrder.get(o1.officeName).compareTo(OfficeOrder.get(o2.officeName));
        if (officeNameCompareResult == 0) {
            return o1.employeeFullName.compareToIgnoreCase(o2.employeeFullName);
        }
        return officeNameCompareResult;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public long getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(long totalHours) {
        this.totalHours = totalHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MonthlyAttendance that = (MonthlyAttendance) o;
        return employeeFullName.equals(that.employeeFullName) && yearMonth.equals(that.yearMonth) && officeName.equals(that.officeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeFullName, yearMonth, officeName);
    }
}
