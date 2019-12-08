package com.fanfx.hourcalculator.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface CountService {
    Workbook count(File file, MultipartFile multipartFile) throws Exception;
}
