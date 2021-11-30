package com.cuit.linzhi.utils;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.*;

public class ExcelReadUtil {


    /**
     * 1、新建workbook，传入excel工作簿文件
     * 2、获取输入流
     * 3、获取sheet对象
     * 4、获取标题栏row对象
     * 5、将标题栏（索引为零的row）输出
     * 6、遍历行列唯一索引坐标，输出每一单元格信息
     * 注：1、需用switch语句判断每一单元格类型
     * 2、xls（HSSFWorkbook）只能读取65535行，但读取速率快（放置在内存中）--数据量大会报异常
     * xlsx无上限限制，但读取速率相对较慢，解决办法：用 （）每次读取100行，多了就存
     *
     * @return
     */

    public List<String> testRead07(String filename) throws Exception {
        List<String> contentList = new LinkedList<String>();
        // 1、获取输入流      C:\\Users\\wzw\\Desktop\\2020.xls
        FileInputStream fileInputStream = new FileInputStream(filename);
        // 2、新建workbook 代表excel表格
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        // 3、获取sheet对象 得到表
        Sheet sheet = workbook.getSheetAt(0);
        // 4、获取标题栏neirong
        Row titleRow = sheet.getRow(0);
        if (titleRow != null) {
            int cellNum = titleRow.getPhysicalNumberOfCells();
            for (int num = 0; num < cellNum; num++) {
                // 4、得到单元格
                Cell titleCell = titleRow.getCell(num);
                if (titleCell != null) {
                    CellType cellType = titleCell.getCellType();
                    String cellValue = titleCell.getStringCellValue();
                    System.out.print("[" + cellValue + "]");
                }
            }
            System.out.println();
        }
        //获取表格内容
        int rowCount = sheet.getPhysicalNumberOfRows();
        for (int rowNum = 0; rowNum < rowCount; rowNum++) {
            int i = 1;
            Row rowData = sheet.getRow(rowNum);
            if (rowData != null) {
                int cellCount = rowData.getPhysicalNumberOfCells();
                for (int cellNumber = 0; cellNumber < cellCount; cellNumber++) {
                    Cell contentCell = rowData.getCell(cellNumber);
                    //匹配数据类型
                    if (contentCell != null) {
                        CellType cellType = contentCell.getCellType();
                        String cellValue = "";
                        cellValue = getCellContent(contentCell, cellType, cellValue);
                        contentList.add(cellValue);
                    }
                }
            }
        }
        fileInputStream.close();

        return contentList;

    }

    /**
     * 根据传入条件获取内容（读取单列内容）
     * 条件包括：文件绝对路径、起始行号、结束行号、列号
     *
     * @return
     */
    private List<String> readContentByTerm(String filePath, int startRow, int endRow, int column) {

        List<String> contentList = new LinkedList<String>();
        try {
            // 1、获取输入流
            FileInputStream fileInputStream = new FileInputStream(filePath);
            // 2、新建workbook 代表excel表格(HSSF代表xls格式)
            Workbook workbook = new HSSFWorkbook(fileInputStream);
            // 3、获取sheet对象 得到表
            Sheet sheet = workbook.getSheetAt(0);

            //遍历起始-结束行，读出对应列的值，并返回列表
            for (int rowIndex = startRow - 1; rowIndex < endRow; rowIndex++) {
                //每次循环读该行对应列的数据
                Row tempRow = sheet.getRow(rowIndex); //获取行对象
                Cell tempCell = tempRow.getCell(column); //获取单元格对象
                if (tempCell != null) {
                    CellType cellType = tempCell.getCellType();
                    String cellValue = tempCell.getStringCellValue();
                    System.out.print("[" + cellValue + "]");
                    contentList.add(cellValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentList;
    }

    /**
     * 根据传入条件获取内容（读取多列内容）
     * 条件包括：文件绝对路径、起始行号、结束行号、列号1、列号2
     *
     * @return
     */
    public static Map<Integer, List<String>> readContentByTerm(String filePath, int startRow, int endRow, int startColumn, int endColumn) {
        Map<Integer, List<String>> contentMap = new HashMap<>();
        List<String> startContent = new LinkedList<String>();
        List<String> endContent = new LinkedList<String>();
        try {
            // 1、获取输入流
            FileInputStream fileInputStream = new FileInputStream(filePath);
            // 2、新建workbook 代表excel表格(HSSF代表xls格式)
            Workbook workbook = new HSSFWorkbook(fileInputStream);
            // 3、获取sheet对象 得到表
            Sheet sheet = workbook.getSheetAt(0);

            //遍历起始-结束行，读出对应列的值，并返回列表
            for (int rowIndex = startRow - 1; rowIndex < endRow + startRow - 1 ; rowIndex++) {
                //每次循环读该行对应列的数据

                Row tempRow = sheet.getRow(rowIndex); //获取行对象
                Cell tempCell1 = tempRow.getCell(startColumn - 1); //获取第一个单元格对象
                Cell tempCell2 = tempRow.getCell(endColumn - 1); //获取第一个单元格对象

                DataFormatter formatter = new DataFormatter();
                String cellValue1 = formatter.formatCellValue(tempCell1);//直接获取到单元格的值

                String cellValue2 = formatter.formatCellValue(tempCell2);//直接获取到单元格的值
                startContent.add(cellValue1);
                endContent.add(cellValue2);


//                if (tempCell1 != null && tempCell2 != null) {
//
//                    CellType cellType1 = tempCell1.getCellType();
//                    String cellValue1 = "";
//                    cellValue1 = getCellContent(tempCell1, cellType1, cellValue1);
//
//                    CellType cellType2 = tempCell1.getCellType();
//                    String cellValue2 = "";
//                    cellValue2 = getCellContent(tempCell2, cellType2, cellValue2);
//
//
//                    startContent.add(cellValue1);
//                    endContent.add(cellValue2);
//                }

            }
            contentMap.put(startColumn,startContent);
            contentMap.put(endColumn,endContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentMap;
    }

    private static String getCellValue(Cell cell) {
        return cell.getStringCellValue();
    }

    private static String getCellContent(Cell contentCell, CellType cellType, String cellValue) {
        switch (cellType) {
            case _NONE:
                break;
            case STRING:
                cellValue = contentCell.getStringCellValue();
                break;
            case ERROR:
                break;
            case BOOLEAN:
                cellValue = String.valueOf(contentCell.getBooleanCellValue());
                break;
            case BLANK:
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(contentCell)) {
                    Date date = contentCell.getDateCellValue();
//                    cellValue = new DateTime(date).toString("yyyy-MM-dd HH:mm:ss");
                } else {
                    cellValue = String.valueOf(contentCell.getNumericCellValue());
                }
                break;
        }
        return cellValue;
    }

}
