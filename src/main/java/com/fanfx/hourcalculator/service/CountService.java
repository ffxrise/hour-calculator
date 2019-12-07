package com.fanfx.hourcalculator.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

public interface CountService {
    Workbook count(MultipartFile file) throws Exception;
}
