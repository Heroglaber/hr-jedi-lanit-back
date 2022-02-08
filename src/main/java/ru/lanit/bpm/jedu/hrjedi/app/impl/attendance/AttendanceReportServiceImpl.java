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
package ru.lanit.bpm.jedu.hrjedi.app.impl.attendance;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.attendance.AttendanceRepository;
import ru.lanit.bpm.jedu.hrjedi.app.api.attendance.AttendanceReportService;
import ru.lanit.bpm.jedu.hrjedi.domain.Attendance;
import ru.lanit.bpm.jedu.hrjedi.domain.Office;

import java.io.IOException;
import java.io.InputStream;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.*;

@Service
public class AttendanceReportServiceImpl implements AttendanceReportService {
    private static final int FIRST_SHEET_INDEX = 0;
    private static final int SECOND_SHEET_INDEX = 1;
    private static final int FIRST_SHEET_START_ROW_INDEX = 3;
    private static final int FIRST_SHEET_START_CELL_INDEX = 0;
    private static final int SECOND_SHEET_START_ROW_INDEX = 1;
    private static final int SECOND_SHEET_START_CELL_INDEX = 0;

    private AttendanceRepository attendanceRepository;

    public AttendanceReportServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public Workbook createAttendanceReport(Month month, int year) {
        InputStream attendanceTemplate = getClass().getResourceAsStream("/reports/attendance.xlsx");
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(attendanceTemplate);
            //data for second sheet
            List<Attendance> attendanceList = attendanceRepository.findAllByMonth(year, month.get(ChronoField.MONTH_OF_YEAR));
            //data for first sheet
            List<AttendanceReportRow> attendanceReportRowList = AttendanceReportRow.reportRowList(attendanceList);
            //sort in required order
            Collections.sort(attendanceReportRowList, new AttendanceReportRow());

            fillFirstSheet(workbook, month, year, attendanceReportRowList);
            fillSecondSheet(workbook, attendanceList);

            XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            return workbook;
        } catch (IOException e) {
            throw new IllegalStateException("Error in attendance report", e);
        }
    }

    private void fillFirstSheet(XSSFWorkbook workbook, Month month, int year, List<AttendanceReportRow> attendanceReportRowList) {
        Sheet firstSheet = workbook.getSheetAt(FIRST_SHEET_INDEX);

        CreationHelper createHelper = workbook.getCreationHelper();
        //fill headings
        Row headerRow = firstSheet.createRow(FIRST_SHEET_START_ROW_INDEX - 2);
        headerRow.createCell(FIRST_SHEET_START_CELL_INDEX + 1).setCellValue(
            createHelper.createRichTextString(month.name()));
        headerRow.createCell(FIRST_SHEET_START_CELL_INDEX + 3).setCellValue(year);
        //fill data
        for(int idx = 0, rowIdx = idx + FIRST_SHEET_START_ROW_INDEX;
                idx < attendanceReportRowList.size();
                idx++, rowIdx++)
        {
            int cellIdx = FIRST_SHEET_START_CELL_INDEX;
            AttendanceReportRow report = attendanceReportRowList.get(idx);
            Row row = firstSheet.createRow(rowIdx);
            row.createCell(cellIdx++).setCellValue(
                createHelper.createRichTextString(report.getEmployeeFullName()));
            row.createCell(cellIdx++).setCellValue(report.getHours());
            row.createCell(cellIdx).setCellValue(
                createHelper.createRichTextString(report.getOfficeName()));
        }
    }

    private void fillSecondSheet(XSSFWorkbook workbook, List<Attendance> attendanceList) {
        Sheet secondSheet = workbook.getSheetAt(SECOND_SHEET_INDEX);

        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("YYYY-mm-dd hh:mm:ss"));

        Map<Long, String> representedOffices = new HashMap<>();
        //fill attendance data
        for(int idx = 0, rowIdx = idx + SECOND_SHEET_START_ROW_INDEX;
            idx < attendanceList.size();
            idx++, rowIdx++)
        {
            int cellIdx = SECOND_SHEET_START_CELL_INDEX;
            Attendance attendance = attendanceList.get(idx);
            Row row = secondSheet.createRow(rowIdx);
            row.createCell(cellIdx++).setCellValue(
                createHelper.createRichTextString(attendance.getEmployee().getFullName()));
            Cell entranceDate = row.createCell(cellIdx++);
            entranceDate.setCellValue(
                Date.from(attendance.getEntranceTime().toInstant(ZoneOffset.UTC)));
            entranceDate.setCellStyle(cellStyle);
            Cell exitDate = row.createCell(cellIdx++);
            exitDate.setCellValue(
                Date.from(attendance.getExitTime().toInstant(ZoneOffset.UTC)));
            exitDate.setCellStyle(cellStyle);
            //Save office to map
            Office office = attendance.getOffice();
            representedOffices.put(office.getId(), office.getName());
            row.createCell(cellIdx).setCellValue(office.getId());
        }
        //fill offices data
        fillOfficesData(secondSheet, createHelper, representedOffices);
    }

    private void fillOfficesData(Sheet sheet, CreationHelper createHelper, Map<Long, String> offices) {
        int rowIdx = SECOND_SHEET_START_ROW_INDEX;
        int cellIdx = SECOND_SHEET_START_CELL_INDEX + 5;
        for(Map.Entry<Long, String> office : offices.entrySet()) {
            Row row = sheet.getRow(rowIdx++);
            row.createCell(cellIdx).setCellValue(office.getKey());
            row.createCell(cellIdx + 1).setCellValue(
                createHelper.createRichTextString(office.getValue()));

        }
    }
}
