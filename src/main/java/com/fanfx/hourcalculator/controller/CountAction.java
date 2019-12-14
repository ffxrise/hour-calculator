package com.fanfx.hourcalculator.controller;

import com.fanfx.hourcalculator.exception.SysCodeEnums;
import com.fanfx.hourcalculator.service.impl.CountServiceImpl;
import com.fanfx.hourcalculator.vo.DataResult;
import com.fanfx.hourcalculator.vo.UploadVo;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class CountAction {
    private ServletContext servletContext;
    private CountServiceImpl countService;

    @Autowired
    public CountAction(ServletContext servletContext, CountServiceImpl countService) {
        this.servletContext = servletContext;
        this.countService = countService;
    }

    private static Logger logger = LoggerFactory.getLogger(CountAction.class);

    @SuppressWarnings("all")
    @PostMapping(value = "/count")
    public DataResult count(@Validated UploadVo vo) throws Exception {
        MultipartFile file = vo.getFile();
        String tmpName = "temp" + file.getOriginalFilename();
        File tempFile = new File(tmpName);
        BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(new File(tmpName)));
        BufferedInputStream bs = new BufferedInputStream(file.getInputStream());
        int r;
        while ((r = bs.read()) != -1) {
            bo.write(r);
        }
        bo.flush();
        bo.close();

        String fileName = "calculated_" + file.getOriginalFilename();
        Workbook workbook = countService.count(tempFile, file);
        File outFile = new File(fileName);
        FileOutputStream fs = new FileOutputStream(outFile);
        workbook.write(fs);
        fs.flush();
        fs.close();
        tempFile.delete();
        return new DataResult(SysCodeEnums.SUCCESS);
    }

    @GetMapping(value = "download")
    public String download(HttpServletResponse response) throws Exception {
//        MediaType mediaType =
//                MediaTypeUtils.getMediaTypeForFileName(this.servletContext, "demo.xls");

        InputStream resourceAsStream =
                getClass().getClassLoader().getResourceAsStream("static/demo.xls");

//        response.setContentType(mediaType.toString());
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=demo.xls");
        response.setContentLength(resourceAsStream.available());

        try (BufferedInputStream inStream = new BufferedInputStream(resourceAsStream)) {
            try (BufferedOutputStream outStream =
                    new BufferedOutputStream(response.getOutputStream())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                outStream.flush();
            }
        }
        return null;
    }
}
