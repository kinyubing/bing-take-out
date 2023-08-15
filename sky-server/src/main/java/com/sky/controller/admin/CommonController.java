package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传接口")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);
        try {
            //获取文件的原始文件名
            String originalFilename = file.getOriginalFilename();
            //获取原始文件名的后缀
            String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
            //随机生成存储在oss上的文件名字拼接上后缀名
            String objectName = (UUID.randomUUID().toString()) + substring;
            //调用文件上传工具类实现文件上传功能
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            //filePath:文件访问的绝对路径
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败{}",e);
        }
        //文件上传失败
        return Result.success(MessageConstant.UPLOAD_FAILED);
    }
}
