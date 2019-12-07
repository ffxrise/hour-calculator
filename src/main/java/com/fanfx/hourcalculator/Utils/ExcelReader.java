package com.fanfx.hourcalculator.Utils;

import com.fanfx.hourcalculator.vo.ExcelDataVO;
import com.google.common.base.Strings;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/** Author: Dreamer-1 Date: 2019-03-01 Time: 10:21 Description: 读取Excel内容 */
public class ExcelReader {

    private static Logger logger = LoggerFactory.getLogger(ExcelReader.class); // 日志打印类

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";

    /**
     * 根据文件后缀名类型获取对应的工作簿对象
     *
     * @param inputStream 读取文件的输入流
     * @param fileType 文件后缀名类型（xls或xlsx）
     * @return 包含文件数据的工作簿对象
     * @throws IOException 异常
     */
    private static Workbook getWorkbook(InputStream inputStream, String fileType)
            throws IOException {
        Workbook workbook = null;
        if (fileType.equalsIgnoreCase(XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileType.equalsIgnoreCase(XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }

    /**
     * 读取Excel文件内容
     *
     * @param fileName 要读取的Excel文件所在路径
     * @return 读取结果列表，读取失败时返回null
     */
    public static List<ExcelDataVO> readExcel(String fileName) {

        Workbook workbook = null;
        FileInputStream inputStream = null;

        try {
            // 获取Excel后缀名
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            // 获取Excel文件
            File excelFile = new File(fileName);
            if (!excelFile.exists()) {
                logger.error("指定的Excel文件不存在！");
                return null;
            }

            // 获取Excel工作簿
            inputStream = new FileInputStream(excelFile);
            workbook = getWorkbook(inputStream, fileType);

            // 读取excel中的数据
            return parseExcel(workbook);
        } catch (Exception e) {
            logger.error("解析Excel失败，文件名：" + fileName + " 错误信息：" + e.getMessage());
            return null;
        } finally {
            try {
                if (null != workbook) {
                    workbook.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.error("关闭数据流出错！错误信息：" + e.getMessage());
            }
        }
    }

    /**
     * 解析Excel数据
     *
     * @param workbook Excel工作簿对象
     * @return 解析结果
     */
    private static List<ExcelDataVO> parseExcel(Workbook workbook) throws Exception {
        List<ExcelDataVO> resultDataList = new ArrayList<>();
        // 解析sheet
        //        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        Sheet sheet = workbook.getSheetAt(2);
        //        sheet.setForceFormulaRecalculation(true);
        // 校验sheet是否合法
        //            if (sheet == null) {
        //                continue;
        //            }
        // 获取第一行数据
        int firstRowNum = sheet.getFirstRowNum();
        //            Row firstRow = sheet.getRow(firstRowNum);
        //            String titles = "排班工号,开始日期,结束日期,开始时间,结束时间,需删除";
        //            String[] titlearr = titles.split(",");
        //            for (int i = 0; i < titlearr.length; i++) {
        //                String titleName = titlearr[i];
        //                Cell cell = firstRow.getCell(i);
        //                try {
        //                    String extitlename = cell.getStringCellValue();
        //                    if (!titleName.equals(extitlename)) {
        //                        throw new Exception();
        //                    }
        //                } catch (Exception e) {
        //                    throw new Exception("表头错误");
        //                }
        //            }

        // 解析每一行的数据，构造数据对象
        int rowStart = firstRowNum+1;
        int rowEnd = sheet.getPhysicalNumberOfRows();
        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (null == row) {
                continue;
            }

            ExcelDataVO resultData = convertRowToData(row, formulaEvaluator);
            resultDataList.add(resultData);
        }
        //        }

        return resultDataList;
    }

    /**
     * 将单元格内容转换为字符串
     *
     * @param cell 内容
     * @param formulaEvaluator
     * @return 内容
     */
    private static Object convertCellValueToString(Cell cell, FormulaEvaluator formulaEvaluator) {
        if (cell == null) {
            return null;
        }
        Object returnValue = null;
        switch (cell.getCellType()) {
            case NUMERIC: // 数字
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    returnValue = cell.getDateCellValue();
                } else {
                    double dValue = cell.getNumericCellValue();
                    DecimalFormat df = new DecimalFormat("0");
                    returnValue = df.format(dValue);
                }
                break;
            case STRING: // 字符串
                returnValue = cell.getStringCellValue();
                break;
            case BOOLEAN: // 布尔
                boolean booleanValue = cell.getBooleanCellValue();
                returnValue = Boolean.toString(booleanValue);
                break;
            case BLANK: // 空值
                break;
            case FORMULA: // 公式
                //                returnValue = cell.getCellFormula();
                returnValue =
                        convertCellValueToString(
                                formulaEvaluator.evaluateInCell(cell), formulaEvaluator);
                break;
            case ERROR: // 故障
                break;
            default:
                break;
        }
        return returnValue;
    }

    /**
     * 提取每一行中需要的数据，构造成为一个结果数据对象
     *
     * <p>当该行中有单元格的数据为空或不合法时，忽略该行的数据
     *
     * @param row 行数据
     * @param formulaEvaluator
     * @return 解析后的行数据对象，行数据错误时返回null
     */
    private static ExcelDataVO convertRowToData(Row row, FormulaEvaluator formulaEvaluator) {
        //        String employeeNr = row.getCell(0).getStringCellValue();
        //        Date startDt = row.getCell(1).getDateCellValue();
        //        Date endDt = row.getCell(2).getDateCellValue();
        //        Date startTm = row.getCell(3).getDateCellValue();
        //        Date endTm = row.getCell(4).getDateCellValue();
        //        String workTmStr = row.getCell(5).getStringCellValue();
        //        return new ExcelDataVO(employeeNr,startDt,endDt,startTm,endTm,workTmStr);
        for (Cell cell : row) {
            Object content = convertCellValueToString(cell, formulaEvaluator);
            if (content == null
                    || (content instanceof String && Strings.isNullOrEmpty((String) content))) {
                continue;
            }
            System.out.println(content);
        }
        return null;
    }
}
