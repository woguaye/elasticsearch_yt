package com.jty.performance.util;

import com.jty.performance.domain.Employee;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * 员工Excel操作工具类
 *
 * @author Jason
 * @since 2019/2/27 13:54
 */
public class ExcelEmployeeUtil {

    /**
     * 导出教师信息表模板
     *
     * @return ByteArrayOutputStream
     */
    public ByteArrayOutputStream exportEmployeeTemplate() {
        String[] title = {"员工姓名（必填）", "职位（必填）", "员工工号（用于账号登录,必填,一次最多录入400个）"};
        return exportTemplate(title, "员工信息表");
    }

//    public ByteArrayOutputStream exportGroupTemplate() {
//        String[] title = {"组名（必填）", "小组描述（可不填）"};
//        return exportTemplate(title, "教师分组信息表");
//    }

    /**
     * 解析Excel2007版
     *
     * @param inputStream inputStream
     * @return List
     */
    public List<Employee> parseExcel2007(InputStream inputStream) {
        try {
            Workbook wb = new XSSFWorkbook(inputStream);
            return parseExcel(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析Excel2003版
     *
     * @param inputStream inputStream
     * @return List
     */
    public List<Employee> parseExcel2003(InputStream inputStream) {
        try {
            Workbook wb = new HSSFWorkbook(inputStream);
            return parseExcel(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析Excel
     *
     * @param wb wb
     * @return List
     */
    public List<Employee> parseExcel(Workbook wb) {
        List<Employee> employeeList = new ArrayList<>();
        Sheet sheet = wb.getSheet("员工信息表");
        if (sheet == null) sheet = wb.getSheetAt(0);
        int rowNum = 2;
        while (true) {
            Row row = sheet.getRow(rowNum++);
            if (row == null) break;
            Employee employee = new Employee();
            if (row.getCell(0) != null) {
                row.getCell(0).setCellType(STRING);
                employee.setEName(row.getCell(0).getStringCellValue());
            }
            if (row.getCell(1) != null) {
                row.getCell(1).setCellType(STRING);
                employee.setEPosition(row.getCell(1).getStringCellValue());
            }
            if (row.getCell(2) != null) {
                row.getCell(2).setCellType(STRING);
                employee.setStaffCode(row.getCell(2).getStringCellValue());
            }
            employeeList.add(employee);
        }
        return employeeList;
    }


    public ByteArrayOutputStream exportEmployee(List<Employee> employeeList) {
        String[] title = {"员工工号", "姓名", "职位", "本期排名", "本期积分", "所在部门"};
        return export(employeeList, title, "员工信息表");
    }

//    public ByteArrayOutputStream exportTeacherGroup(List<TeacherGroupCountBo> teacherGroupList) {
//        String[] title = {"组名", "人数"};
//        return exportGroup(teacherGroupList, title, "分组表");
//    }


    public ByteArrayOutputStream export(List<Employee> list, String[] title, String name) {
        Workbook wb = new XSSFWorkbook();
        Map<String, CellStyle> styles = createStyles(wb);

        Sheet sheet = wb.createSheet(name);
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(25.75f);
        Cell headCell = headerRow.createCell(0);
        headCell.setCellValue(name);
        headCell.setCellStyle(styles.get("title"));
        Row row = sheet.createRow(1);
        Cell cell;
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(styles.get("header"));
        }
        if (list != null && !list.isEmpty()) {
            int i = 2;
            for (Employee one : list) {
                Row cellRow = sheet.createRow(i++);
                cell = cellRow.createCell(0);
                cell.setCellValue(one.getStaffCode());
                cell.setCellStyle(styles.get("cell"));
                cell = cellRow.createCell(1);
                cell.setCellValue(one.getEName());
                cell.setCellStyle(styles.get("cell"));
                cell = cellRow.createCell(2);
                cell.setCellValue(one.getEPosition());
                cell.setCellStyle(styles.get("cell"));
                cell = cellRow.createCell(3);
                cell.setCellValue("本期排名");
                cell.setCellStyle(styles.get("cell"));
                cell = cellRow.createCell(4);
//                if (one.getLastLoginTime() != null) {
//                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    cell.setCellValue(dateTimeFormatter.format(one.getLastLoginTime()));
//                } else {
//                    cell.setCellValue("");
//                }
                cell.setCellValue(one.getCurrentScore());
                cell.setCellStyle(styles.get("cell"));
                cell = cellRow.createCell(5);
                cell.setCellValue(one.getDepartment().getDeptName());
                cell.setCellStyle(styles.get("cell"));
            }
        }
        // 设置列宽度
        sheet.setColumnWidth(0, 256 * 10);
        sheet.setColumnWidth(1, 256 * 30);
        sheet.setColumnWidth(2, 256 * 30);
        sheet.setColumnWidth(3, 256 * 20);
        sheet.setColumnWidth(4, 256 * 30);
        sheet.setColumnWidth(5, 256 * 20);
//        sheet.setColumnWidth(6, 256 * 20);

        CellRangeAddress headRegion = new CellRangeAddress(0, 0, 0, title.length - 1);
        sheet.addMergedRegion(headRegion);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            wb.write(out);
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

//    public ByteArrayOutputStream exportGroup(List<TeacherGroupCountBo> list, String[] title, String name) {
//        Workbook wb = new XSSFWorkbook();
//        Map<String, CellStyle> styles = createStyles(wb);
//
//        Sheet sheet = wb.createSheet(name);
//        Row headerRow = sheet.createRow(0);
//        headerRow.setHeightInPoints(25.75f);
//        Cell headCell = headerRow.createCell(0);
//        headCell.setCellValue(name);
//        headCell.setCellStyle(styles.get("title"));
//        Row row = sheet.createRow(1);
//        Cell cell;
//        for (int i = 0; i < title.length; i++) {
//            cell = row.createCell(i);
//            cell.setCellValue(title[i]);
//            cell.setCellStyle(styles.get("header"));
//        }
//        if (list != null && !list.isEmpty()) {
//            int i = 2;
//            for (TeacherGroupCountBo one : list) {
//                Row cellRow = sheet.createRow(i++);
//                cell = cellRow.createCell(0);
//                cell.setCellValue(one.getName());
//                cell.setCellStyle(styles.get("cell"));
//                cell = cellRow.createCell(1);
//                cell.setCellValue(one.getCount());
//                cell.setCellStyle(styles.get("cell"));
//            }
//        }
//        // 设置列宽度
//        sheet.setColumnWidth(0, 256 * 30);
//        sheet.setColumnWidth(1, 256 * 10);
//
//        CellRangeAddress headRegion = new CellRangeAddress(0, 0, 0, title.length - 1);
//        sheet.addMergedRegion(headRegion);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        try {
//            wb.write(out);
//            wb.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return out;
//    }


    public ByteArrayOutputStream exportTemplate(String[] title, String name) {
        Workbook wb = new XSSFWorkbook();
        Map<String, CellStyle> styles = createStyles(wb);

        Sheet sheet = wb.createSheet(name);
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(25.75f);
        Cell headCell = headerRow.createCell(0);
        headCell.setCellValue(name);
        headCell.setCellStyle(styles.get("title"));

        Row row = sheet.createRow(1);
        Cell cell;
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(styles.get("header"));
            sheet.setColumnWidth(i, 256 * 20);
        }
        // 合并第一行
        CellRangeAddress headRegion = new CellRangeAddress(0, 0, 0, title.length - 1);
        sheet.addMergedRegion(headRegion);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            wb.write(out);
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Create a library of cell styles
     */
    private static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle style;
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(titleFont);
        styles.put("title", style);

        Font monthFont = wb.createFont();
        monthFont.setFontHeightInPoints((short) 11);
        monthFont.setColor(IndexedColors.WHITE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        styles.put("header", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styles.put("cell", style);

        return styles;
    }
}
