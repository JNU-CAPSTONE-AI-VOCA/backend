package kr.ac.jnu.vocai.backend.file.parser.impl;

import kr.ac.jnu.vocai.backend.file.parser.TextParser;
import kr.ac.jnu.vocai.backend.file.parser.exception.PageIndexOutOfBoundsException;

import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * .pdf 파일의 내용을 text 로 parsing 하는 parser 클래스.
 * @author daecheol song
 * @since 1.0
 */
@Slf4j
public class PdfTextParser implements TextParser {

    public static final String FILE_SUFFIX = ".pdf";
    public static final String EMPTY_STRING = "";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(String fileName) {
        if (!fileName.contains(".")) {
            return false;
        }
        return fileName.substring(fileName.indexOf(".")).equalsIgnoreCase(FILE_SUFFIX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parse(File file) {
        ExtractedTextFormatter extractedTextFormatter = ExtractedTextFormatter.builder()
                .withLeftAlignment(true)
                .withNumberOfBottomTextLinesToDelete(1)
                .build();
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig
                .builder()
                .withPageExtractedTextFormatter(extractedTextFormatter)
                .build();


        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(ResourceUtils.FILE_URL_PREFIX + file.getAbsolutePath(), config);

        return pdfReader.get()
                .stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parseByPage(File file, int page) {
        try (PDDocument pdfDocument = Loader.loadPDF(file)) {
            int numberOfPages = pdfDocument.getNumberOfPages();
            if (page <= 0 || page > numberOfPages) {
                throw new PageIndexOutOfBoundsException("주어진 page 가 파일의 page 인덱스 범위를 벗어났습니다. page= " + page + ", numberOfPages = " + numberOfPages);
            }
            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setLineSeparator("\n");
            textStripper.setWordSeparator(" ");
            Splitter splitter = new Splitter();
            splitter.setStartPage(page);
            splitter.setEndPage(page);
            return Arrays.stream(textStripper
                            .getText(splitter.split(pdfDocument).get(0))
                            .split("\n"))
                    .filter(s -> !s.isBlank())
                    .map(String::toLowerCase)
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error("해당 파일로부터 pdf 를 로드할 수 없습니다. fileName = {}, cause = {}", file.getName(), e.getMessage());
        }
        return EMPTY_STRING;
    }
}
