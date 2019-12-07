package com.fanfx.hourcalculator.Demo;

import com.fanfx.hourcalculator.Utils.ExcelReader;

import java.io.File;

public class Calculate {

    public static void main(String[] args) {
        String path = "C:\\Users\\123\\Desktop\\doc";
        File file = new File(path);
        File[] files = file.listFiles();
        File excel = files[0];
        ExcelReader.readExcel(excel.getPath());
    }
}
