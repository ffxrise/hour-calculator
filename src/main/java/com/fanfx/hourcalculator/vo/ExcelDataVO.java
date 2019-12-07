package com.fanfx.hourcalculator.vo;

import lombok.Data;

import java.util.Date;

/** Author: Dreamer-1 Date: 2019-03-01 Time: 11:33 Description: 读取Excel时，封装读取的每一行的数据 */
@Data
public class ExcelDataVO {

    /**
     * 工号
     */
    private String employeeNr;

    /**
     * 开始日期
     */
    private Date startDt;

    /**
     * 结束日期
     */
    private Date endDt;

    /** 开始时间 */
    private Date startTm;

    /** 结束时间 */
    private Date endTm;

    /**
     * 正常工作时间
     */
    private String workTm;

    /**
     * 加班时间
     */
    private String overTm;

    public ExcelDataVO(String employeeNr, Date startDt, Date endDt, Date startTm, Date endTm, String workTm) {
        this.employeeNr = employeeNr;
        this.startDt = startDt;
        this.endDt = endDt;
        this.startTm = startTm;
        this.endTm = endTm;
        this.workTm = workTm;
    }

    public ExcelDataVO() {
    }
}
