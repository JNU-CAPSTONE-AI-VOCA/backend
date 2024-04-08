package kr.ac.jnu.vocai.backend.file.controller;

import kr.ac.jnu.vocai.backend.common.utils.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 파일 업로드 컨트롤러 클래스.
 * @author wavewwave20
 * @since 1.0
 */
@Slf4j
@Controller
public class FileUploadController {

    private String uploadDir = "c:/dev/postman/uploaded";

    private FileUploadUtils fileUploadUtils;

    @Autowired
    public FileUploadController(FileUploadUtils fileUploadUtils) {
        this.fileUploadUtils = fileUploadUtils;
    }

    @PostMapping("/pdf/upload")
    public ResponseEntity<String> saveFiles(@RequestParam("files") MultipartFile[] files) throws IOException {

        if (files.length > 3) {
            return ResponseEntity.badRequest().body("cannot upload more than 3 files");
        }
        if (files.length == 0) {
            return ResponseEntity.badRequest().body("no files to upload");
        }
        for (MultipartFile file : files) {
            log.info("file name: {}", file.getOriginalFilename());
            log.info("file size: {}", file.getSize());
            log.info("file content type: {}", file.getContentType());

            // 파일 저장
            file.transferTo(new File(uploadDir + "/" + FileUploadUtils.fileNameConvert(file.getOriginalFilename())));
        }
        return ResponseEntity.ok("upload success");
    }

}
