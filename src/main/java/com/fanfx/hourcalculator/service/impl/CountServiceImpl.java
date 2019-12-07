package com.fanfx.hourcalculator.service.impl;

import com.fanfx.hourcalculator.Utils.DateTool;
import com.fanfx.hourcalculator.service.CountService;
import com.google.common.base.Strings;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class CountServiceImpl implements CountService {

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";
    private static final String TITLES =
            "序号,班组,排班工号,姓名,类型,名称,开始日期,结束日期,开始时间,结束时间,需删除,是否有加班,活动量,补时(分钟),补量(分钟),手工补量(次数),描述,操作人,操作人姓名,操作日期";

    @Override
    public Workbook count(MultipartFile file) throws Exception {
        return readExcel(file);

    }

    private Workbook readExcel(MultipartFile file) throws Exception {
        Workbook workbook;
        InputStream inputStream = file.getInputStream();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        workbook = getWorkbook(inputStream, fileType);
        checkExcel(workbook);
        Sheet sheet = workbook.getSheetAt(2);
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        // 解析每一行的数据，构造数据对象
        int rowStart = sheet.getFirstRowNum() + 1;
        int rowEnd = sheet.getLastRowNum();
        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (null == row) {
                continue;
            }
            Date begin = null;
            Date end = null;
            String workTm;
            List<Cell> list = new ArrayList<>();
            try {
                Date startDt = (Date) convertCellValueToString(row.getCell(6), formulaEvaluator,list);
                Date endDt = (Date) convertCellValueToString(row.getCell(7), formulaEvaluator,list);
                Date startTm = (Date) convertCellValueToString(row.getCell(8), formulaEvaluator,list);
                Date endTm = (Date) convertCellValueToString(row.getCell(9), formulaEvaluator,list);
                workTm = (String) convertCellValueToString(row.getCell(10), formulaEvaluator,list);

                begin = DateTool.combine(startDt, startTm);
                end = DateTool.combine(endDt, endTm);
                if (begin == null || end == null || Strings.isNullOrEmpty(workTm)||begin.after(end)) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }finally{
                for (Cell cell : list) {
                    formulaEvaluator.evaluateFormulaCell(cell);
                }
            }

            int overTm = getOverMin(begin, end, workTm);
            Cell cell = row.createCell(20, CellType.NUMERIC);
            cell.setCellValue(overTm);
        }
        Row row = sheet.getRow(0);
        Cell cell = row.createCell(20, CellType.STRING);
        cell.setCellValue("加班分钟数");
        return workbook;
    }

    /**
     * 计算加班分钟数
     *
     * @param begin 开始加班时间
     * @param end 结束加班时间
     * @param workTms 正常工作时间
     * @return 不在正常工作时间的分钟数
     */
    private int getOverMin(Date begin, Date end, String workTms) {
        int min = 0;
        Map<Date, Date> wdMap = new HashMap<>();
        String yyyymmdd = DateTool.dateToYyyymmdd(begin);
        String[] arr = workTms.split(";");
        for (String s : arr) {
            String[] tm = s.split("~");
            if (tm.length != 2) {
                continue;
            }
            wdMap.put(
                    DateTool.paresDate(yyyymmdd + " " + tm[0], "yyyy-MM-dd HH:mm"),
                    DateTool.paresDate(yyyymmdd + " " + tm[1], "yyyy-MM-dd HH:mm"));
        }

        for (Date wStart : wdMap.keySet()) {
            Date wEnd = wdMap.get(wStart);
            if (begin.getTime()>=wStart.getTime()&&end.getTime()<=wEnd.getTime()){
                wdMap.remove(wStart);
            }
        }

        Date temp = new Date(begin.getTime());
        if (wdMap.size()>0){
            while (temp.getTime()<=end.getTime()){
                int count = 1;
                for (Date wStart : wdMap.keySet()) {
                    Date wEnd = wdMap.get(wStart);
                    if (temp.getTime()>=wStart.getTime()&&temp.getTime()<=wEnd.getTime()){
                        break;
                    }else if (count<wdMap.size()){
                        count++;
                        continue;
                    }
                    min++;
                }

                temp = DateTool.addMin(temp, 1);
            }
        }
        return min;
    }

    /**
     * 将单元格内容转换为对象
     *
     * @param cell 内容
     * @param formulaEvaluator 公式转对象
     * @param list
     * @return 内容
     */
    private static Object convertCellValueToString(Cell cell, FormulaEvaluator formulaEvaluator, List<Cell> list) {
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
                //                returnValue = cell.getCellFormula()
                returnValue = convertCellValueToString(formulaEvaluator.evaluateInCell(cell),formulaEvaluator,list);
                list.add(cell);
                break;
            case ERROR: // 故障
                break;
            default:
                break;
        }
        return returnValue;
    }

    private void checkExcel(Workbook workbook) throws Exception {
        if (workbook == null) {
            throw new Exception("文件错误！");
        }

        Sheet sheet = workbook.getSheetAt(2);
        if (sheet == null) {
            throw new Exception("文件样式错误，请下载模板参考");
        }
        Row firstRow = sheet.getRow(sheet.getFirstRowNum());
        short lastCellNum = firstRow.getLastCellNum();
        String[] titlearr = TITLES.split(",");
        if (lastCellNum != titlearr.length) {
            throw new Exception("文件样式错误，请下载模板参考");
        }

        for (int i = 0; i < titlearr.length; i++) {
            String titleName = titlearr[i];
            Cell cell = firstRow.getCell(i);
            try {
                String extitlename = cell.getStringCellValue();
                if (!titleName.equals(extitlename)) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new Exception("表头错误");
            }
        }


    }

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
}
