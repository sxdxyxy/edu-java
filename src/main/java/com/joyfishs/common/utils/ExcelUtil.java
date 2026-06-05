package com.joyfishs.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel工具类
 * 提供Excel导入导出功能
 *
 * @author OpenClaw
 * @since 2026-03-31
 */
public class ExcelUtil {

    /**
     * 导出Excel
     *
     * @param data 数据列表
     * @param response HTTP响应对象
     * @param fileName 文件名
     * @throws IOException
     */
    public static <T> void exportExcel(List<T> data, HttpServletResponse response, String fileName) throws IOException {
        if (data == null || data.isEmpty()) {
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // 获取实体类的字段信息
        Class<?> clazz = data.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        int columnIndex = 0;
        List<Field> validFields = new ArrayList<>();
        
        for (Field field : fields) {
            // 忽略类和方法字段，只处理实际数据字段
            if (!field.getName().equals("serialVersionUID")) {
                headerRow.createCell(columnIndex).setCellValue(formatFieldName(field.getName()));
                field.setAccessible(true);
                validFields.add(field);
                columnIndex++;
            }
        }

        // 填充数据行
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            T item = data.get(i);
            
            for (int j = 0; j < validFields.size(); j++) {
                Field field = validFields.get(j);
                Cell cell = row.createCell(j);
                
                try {
                    Object value = field.get(item);
                    if (value == null) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(value.toString());
                    }
                } catch (IllegalAccessException e) {
                    cell.setCellValue("");
                }
            }
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 导入Excel
     *
     * @param inputStream 输入流
     * @param clazz 目标类类型
     * @return 数据列表
     * @throws Exception
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) throws Exception {
        List<T> result = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        if (sheet == null || sheet.getLastRowNum() <= 0) {
            return result;
        }

        // 获取表头信息
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return result;
        }

        // 建立表头和字段的映射关系
        Map<String, Field> headerFieldMap = new HashMap<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        
        // 创建字段名映射，将格式化的字段名与实际字段关联
        Map<String, Field> formattedFieldMap = new HashMap<>();
        for (Field field : declaredFields) {
            if (!field.getName().equals("serialVersionUID")) {
                field.setAccessible(true);
                formattedFieldMap.put(formatFieldName(field.getName()), field);
            }
        }

        // 遍历表头行，建立表头与字段的映射
        for (int colIndex = 0; colIndex < headerRow.getLastCellNum(); colIndex++) {
            Cell cell = headerRow.getCell(colIndex);
            if (cell != null) {
                String headerName = getCellValueAsString(cell);
                Field field = formattedFieldMap.get(headerName);
                if (field != null) {
                    headerFieldMap.put(headerName, field);
                }
            }
        }

        // 从第二行开始读取数据
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }

            T instance = clazz.getDeclaredConstructor().newInstance();

            for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell != null) {
                    String headerName = getCellValueAsString(headerRow.getCell(colIndex));
                    Field field = headerFieldMap.get(headerName);
                    
                    if (field != null) {
                        Object value = convertCellValue(cell, field.getType());
                        field.set(instance, value);
                    }
                }
            }

            result.add(instance);
        }

        workbook.close();
        return result;
    }

    /**
     * 将字段名转换为显示名称（驼峰转下划线形式）
     */
    private static String formatFieldName(String fieldName) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (Character.isUpperCase(c) && i != 0) {
                result.append(" ");
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString();
    }

    /**
     * 获取单元格的字符串值
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long)cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * 转换单元格值为目标类型
     */
    private static Object convertCellValue(Cell cell, Class<?> targetType) {
        if (cell == null) {
            return null;
        }

        String cellValue = getCellValueAsString(cell);
        if (cellValue == null || cellValue.trim().isEmpty()) {
            return null;
        }

        if (targetType == String.class) {
            return cellValue;
        } else if (targetType == Integer.class || targetType == int.class) {
            try {
                return Integer.parseInt(cellValue);
            } catch (NumberFormatException e) {
                return 0;
            }
        } else if (targetType == Long.class || targetType == long.class) {
            try {
                return Long.parseLong(cellValue);
            } catch (NumberFormatException e) {
                return 0L;
            }
        } else if (targetType == Double.class || targetType == double.class) {
            try {
                return Double.parseDouble(cellValue);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else if (targetType == Float.class || targetType == float.class) {
            try {
                return Float.parseFloat(cellValue);
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(cellValue);
        } else if (targetType == java.util.Date.class || targetType == java.sql.Date.class) {
            try {
                return java.sql.Date.valueOf(cellValue);
            } catch (IllegalArgumentException e) {
                return null;
            }
        } else {
            // 默认返回字符串
            return cellValue;
        }
    }
}