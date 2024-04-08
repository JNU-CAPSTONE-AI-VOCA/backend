package kr.ac.jnu.vocai.backend.controller;

import kr.ac.jnu.vocai.backend.file.FileUploadUtils;
import kr.ac.jnu.vocai.backend.file.dto.VocabularyListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 파일 업로드 컨트롤러 클래스.
 * @author wavewwave20
 * @since 1.0
 */
@Slf4j
@Controller
public class UploadController {

    private String uploadDir = "c:/dev/postman/uploaded";

    private FileUploadUtils fileUploadUtils;

    @Autowired
    public UploadController(FileUploadUtils fileUploadUtils) {
        this.fileUploadUtils = fileUploadUtils;
    }

    @PostMapping("/pdf/upload")
    public ResponseEntity<?> saveFiles(@RequestParam("files") MultipartFile[] files) throws IOException {

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

        VocabularyListDTO.WordDTO wordDTO1 = new VocabularyListDTO.WordDTO("apple", "사과");
        VocabularyListDTO.WordDTO wordDTO2 = new VocabularyListDTO.WordDTO("grape", "포도");
        VocabularyListDTO.WordDTO wordDTO3 = new VocabularyListDTO.WordDTO("banana", "바나나");
        VocabularyListDTO.WordDTO wordDTO4 = new VocabularyListDTO.WordDTO("melon", "멜론");
        VocabularyListDTO.ExampleDTO exampleDTO1 = new VocabularyListDTO.ExampleDTO("apple and grape", List.of(wordDTO1, wordDTO2));
        VocabularyListDTO.ExampleDTO exampleDTO2 = new VocabularyListDTO.ExampleDTO("banana and melon", List.of(wordDTO3, wordDTO4));

        List<VocabularyListDTO.ExampleDTO> exampleDTOList = new java.util.ArrayList<>(List.of(exampleDTO1, exampleDTO2));

        VocabularyListDTO vocabularyListDTO = new VocabularyListDTO(exampleDTOList);


        return ResponseEntity.ok(vocabularyListDTO);
    }



}
