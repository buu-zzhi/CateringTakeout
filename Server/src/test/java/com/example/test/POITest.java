package com.example.test;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@SpringBootTest
public class POITest {
    public static void write() throws Exception {
        // 在内存中创建
        XSSFWorkbook excel = new XSSFWorkbook();
        // 创建excel工作表
        XSSFSheet sheet = excel.createSheet("info");
        // 创建行
        XSSFRow row = sheet.createRow(1);
        List<String> table = Arrays.asList("name", "hobby");
        for (int i = 0; i < table.size(); i++) {
            row.createCell(i).setCellValue(table.get(i));
        }
        row = sheet.createRow(2);
        row.createCell(0).setCellValue("Hangfan");
        row.createCell(1).setCellValue("football");

        FileOutputStream stream = new FileOutputStream(new File("D:\\code\\resource\\info.xlsx"));
        excel.write(stream);
        excel.close();
        stream.close();
    }

    public static void read() throws Exception {
        InputStream in = new FileInputStream(new File("D:\\code\\resource\\info.xlsx"));
        XSSFWorkbook excel = new XSSFWorkbook(in);
        // 读取工作表，根据表明/下标
        XSSFSheet sheet = excel.getSheetAt(0);

        int lastRowNum = sheet.getLastRowNum();
        for (int i = 1; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            String stringCellValue1 = row.getCell(0).getStringCellValue();
            String stringCellValue2 = row.getCell(1).getStringCellValue();
            System.out.println(stringCellValue1 + ", " + stringCellValue2);
        }
        excel.close();
        in.close();
    }

    public static void main(String[] args) throws Exception {
//        write();
        read();
    }
}
