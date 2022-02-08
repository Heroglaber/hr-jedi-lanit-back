package ru.lanit.bpm.jedu.hrjedi.app.impl.attendance;

import ru.lanit.bpm.jedu.hrjedi.domain.Attendance;

import java.time.Duration;
import java.util.*;

/**
 * AttendanceReportRow represents a row for the attendance report:
 * employee name | attendance hours for specified month | employee's office
 */
public class AttendanceReportRow implements Comparator<AttendanceReportRow> {
    private String employeeFullName;
    private long hours;
    private String officeName;

    public static List<AttendanceReportRow> reportRowList (List<Attendance> attendanceList) {
        Map<AttendanceReportRow, List<Attendance>> reportRowMap = new HashMap<>();
        for(Attendance attendance : attendanceList) {
            AttendanceReportRow key = new AttendanceReportRow(attendance);
            if(reportRowMap.containsKey(key)) {
                reportRowMap.get(key).add(attendance);
            } else {
                reportRowMap.put(key, new ArrayList<>(Arrays.asList(attendance)));
            }
        }
        List<AttendanceReportRow> reportRowList = new ArrayList<>();
        for (Map.Entry<AttendanceReportRow, List<Attendance>> entry : reportRowMap.entrySet()) {
            AttendanceReportRow reportRow = entry.getKey();
            Duration total = Duration.ZERO;
            for(Attendance attendance : entry.getValue()) {
                total = total.plus(Duration.between(attendance.getEntranceTime(), attendance.getExitTime()));
            }
            reportRow.setHours(toHours(total));
            reportRowList.add(reportRow);
        }
        return reportRowList;
    }

    private static int toHours(Duration duration) {
        double secondsInHour = 3600.0;
        return (int) Math.ceil(duration.getSeconds()/secondsInHour);
    }

    @Override
    public int compare(AttendanceReportRow o1, AttendanceReportRow o2) {
        int officeNameCompareResult = OfficeOrder.get(o1.officeName).compareTo(OfficeOrder.get(o2.officeName));
        if(officeNameCompareResult == 0) {
            return o1.employeeFullName.compareToIgnoreCase(o2.employeeFullName);
        }
        return officeNameCompareResult;
    }

    public AttendanceReportRow(Attendance attendance) {
        this(attendance.getEmployee().getFullName(), attendance.getOffice().getName());
    }

    public AttendanceReportRow(String employeeFullName, String officeName) {
        this.employeeFullName = employeeFullName;
        this.officeName = officeName;
    }

    public AttendanceReportRow() {}

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttendanceReportRow that = (AttendanceReportRow) o;
        return employeeFullName.equals(that.employeeFullName) && officeName.equals(that.officeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeFullName, officeName);
    }
}
