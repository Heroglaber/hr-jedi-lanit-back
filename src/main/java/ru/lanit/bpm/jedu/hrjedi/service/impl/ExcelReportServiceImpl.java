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
package ru.lanit.bpm.jedu.hrjedi.service.impl;

import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.lanit.bpm.jedu.hrjedi.rest.StreamedResult;
import ru.lanit.bpm.jedu.hrjedi.rest.WorkbookResult;
import ru.lanit.bpm.jedu.hrjedi.service.ExcelReportService;

import java.io.IOException;
import java.io.InputStream;
import java.time.Month;

@Service
public class ExcelReportServiceImpl implements ExcelReportService {
    @Override
    public StreamedResult createAttendanceReport(Month month, int year) {
        InputStream attendanceTemplate = getClass().getResourceAsStream("/reports/attendance.xlsx");
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(attendanceTemplate);

            // now we just downloading empty report

            XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            return new WorkbookResult(workbook);
        } catch (IOException e) {
            throw new IllegalStateException("Error in attendance report", e);
        }
    }
}
