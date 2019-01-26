

package com.yq.controller;

import com.yq.client.FileServiceClient;
import com.yq.client.UserServiceClient;
import com.yq.domain.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;


@RestController
@RequestMapping("/my")
@Slf4j
public class FileController {

    @Autowired
    private FileServiceClient fileSvcClient;

    @ApiOperation(value = "上传文件， 演示服务间调用传递文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content", defaultValue = "abcd", value = "文件内容", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "fileName",defaultValue = "fileRule01.txt", value = "文件名称", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/testFileUpload", produces = "application/json;charset=UTF-8")
    public String testUploadFile(@RequestParam(required = true, defaultValue = "fileRule01.txt") String fileName,
                                        @RequestParam(required = true, defaultValue = "abcd") String content,
                                        HttpServletRequest request) {
        String result = "not upload";

        try {
            byte[] bytes = content.getBytes("UTF-8");
            MultipartFile multipartFile = new MockMultipartFile("file", fileName, "text/plain", bytes);

            String comment = "演示FeignClient文件上传";
            result = fileSvcClient.uploadFile(multipartFile, "mypath1/", comment);

        }
        catch (UnsupportedEncodingException ex) {
            log.warn("Failed to convert content={} to bytes", content, ex);
        }

        return result;
    }

    @ApiOperation(value = "上传文件， 演示服务间调用传递文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", defaultValue = "file1.txt", value = "文件内容", required = true, dataType = "file", paramType = "query")

    })
    @PostMapping(value = "/testFileUploadDirectly", produces = "application/json;charset=UTF-8")
    public String testUploadFileDirectly(@RequestParam("file") MultipartFile file) {
        String result = "not upload";

        try {
            MultipartFile multipartFile = file;

            String comment = "演示FeignClient文件上传";
            result = fileSvcClient.uploadFile(multipartFile, "mypath1/", comment);

        }
        catch (Exception ex) {
            log.warn("Failed to upload", ex);
        }

        return result;
    }

}