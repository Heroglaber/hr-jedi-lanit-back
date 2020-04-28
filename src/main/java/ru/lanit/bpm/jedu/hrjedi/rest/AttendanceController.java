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
package ru.lanit.bpm.jedu.hrjedi.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import ru.lanit.bpm.jedu.hrjedi.model.Attendance;
import ru.lanit.bpm.jedu.hrjedi.service.AttendanceService;
import ru.lanit.bpm.jedu.hrjedi.service.ExcelReportService;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping("/hr-rest/attendances")
public class AttendanceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceController.class);
    private static final String ATTENDANCE_REPORT_NAME = "Отчет о посещаемости за %s %s.xlsx";
    private static final String ATTENDANCE_REPORT_NAME_ASCII = "Attendance Report.xlsx";

    private AttendanceService attendanceService;
    private ExcelReportService excelReportService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService, ExcelReportService excelReportService) {
        this.attendanceService = attendanceService;
        this.excelReportService = excelReportService;
    }

    @GetMapping("/employees/{id}")
    @PreAuthorize("hasRole('OMNI') or hasRole('HR')")
    public List<Attendance> getAllAttendancesByEmployeeId(@PathVariable("id") Long employeeId) {
        Assert.notNull(employeeId, "Employee id cannot be null");
        return attendanceService.findAllByEmployeeId(employeeId);
    }

    @GetMapping("/monthsWithoutInfo/{year}")
    @PreAuthorize("hasRole('OMNI') or hasRole('HR')")
    public List<YearMonth> getMonthsWithoutAttendanceInfoByYear(@PathVariable("year") Integer year) {
        Assert.notNull(year, "Provided year cannot be null");
        return attendanceService.getMonthsWithoutAttendanceInfoByYear(year);
    }

    @GetMapping("/month-report/download")
    public void downloadAttendanceReport(@RequestParam("month") Month month, @RequestParam("year") int year, HttpServletResponse response) {
        StreamedResult result = excelReportService.createAttendanceReport(month, year);
        DownloadableFile reportFile = new DownloadableFile(String.format(ATTENDANCE_REPORT_NAME, year, month), ATTENDANCE_REPORT_NAME_ASCII, result);
        writeFileToResponse(reportFile, response);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    protected void writeFileToResponse(DownloadableFile downloadableFile, HttpServletResponse response) {
        response.setContentType(downloadableFile.getContentType());
        response.addHeader(CONTENT_DISPOSITION,
            "attachment; filename=\"" + downloadableFile.getAsciiName() + "\"; filename*=UTF-8''" + encodeRfc5987(downloadableFile.getUtf8Name()));

        try (OutputStream outputStream = response.getOutputStream()) {
            downloadableFile.write(outputStream);
            outputStream.flush();
        } catch (Exception ex) {
            LOGGER.debug("Error on completing response", ex);
        }
    }

    protected String encodeRfc5987(String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        final StringBuilder sb = new StringBuilder(bytes.length << 1);
        final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        final byte[] attrChar = {'!', '#', '$', '&', '+', '-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '|', '~'};
        for (final byte b : bytes) {
            if (Arrays.binarySearch(attrChar, b) >= 0) {
                sb.append((char) b);
            } else {
                sb.append('%');
                sb.append(digits[0x0f & (b >>> 4)]);
                sb.append(digits[b & 0x0f]);
            }
        }
        return sb.toString();
    }
}
