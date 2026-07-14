package com.aizuche.util;

import com.aizuche.entity.Vehicle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelUtil {

    public static String exportVehicleList(List<Vehicle> list, String[] fields) {
        String fileName = "车辆数据_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        String filePath = "./exports/" + fileName;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("车辆列表");

            // 创建表头
            Row header = sheet.createRow(0);
            String[] headers = {"品牌", "型号", "车牌", "颜色", "座位数", "日租金", "状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(getHeaderStyle(workbook));
            }

            // 填充数据
            int rowNum = 1;
            for (Vehicle v : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getBrand());
                row.createCell(1).setCellValue(v.getModel());
                row.createCell(2).setCellValue(v.getPlateNumber());
                row.createCell(3).setCellValue(v.getColor());
                row.createCell(4).setCellValue(v.getSeatCount());
                row.createCell(5).setCellValue(v.getDailyPrice().doubleValue());
                row.createCell(6).setCellValue(v.getStatus());
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 保存文件
            java.io.File dir = new java.io.File("./exports");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePath;
    }

    private static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}