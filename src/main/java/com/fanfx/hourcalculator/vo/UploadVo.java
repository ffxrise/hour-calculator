package com.fanfx.hourcalculator.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class UploadVo {
    @NotNull(message = "文件不存在")
    private MultipartFile file;
}

