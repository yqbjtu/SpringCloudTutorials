
package com.yq.client;


import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * Simple to Introduction
 * className: FileServiceClient
 *
 * @author EricYang
 * @version 2018/12/07 19:50
 */

@FeignClient(value = "file-service", fallbackFactory = FileServiceFactory.class)
public interface FileServiceClient {

    /**
     * @param filePath
     * @param userId
     * @param comment
     * @return
     */
    @PostMapping(value = "/v1/file/uploadFile", produces = "application/json;charset=UTF-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("filePath") String filePath,
                      @RequestParam("userId") String userId,
                      @RequestParam("comment") String comment);

}

@Component
@Slf4j
class FileServiceFactory implements FallbackFactory<FileServiceClient> {
    @Override
    public FileServiceClient create(final Throwable throwable) {
        return new FileServiceClient() {

            @Override
            public String uploadFile(MultipartFile file, @RequestParam("filePath") String filePath,
                       @RequestParam("userId") String userId,
                       @RequestParam("comment") String comment) {
                log.warn("Failed to uploadFile. Fallback reason = {}", throwable.getMessage());
                throw new RuntimeException(throwable.getCause());
            }
        };
    }
}