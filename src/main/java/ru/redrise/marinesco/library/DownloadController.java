package ru.redrise.marinesco.library;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import ru.redrise.marinesco.settings.ApplicationSettings;

@Controller
@RequestMapping("/download")
public class DownloadController {

    private final String filesLocation;

    public DownloadController(ApplicationSettings applicationSettings) {
        this.filesLocation = applicationSettings.getFilesLocation();
    }

    @GetMapping(value = "/")
    public void getMethodName(@RequestParam String container,
            @RequestParam String file,
            HttpServletResponse response) throws Exception {
        try {
            File bookFile = new File(filesLocation + File.separator + container + File.separator + file);
            if (! bookFile.exists())
                throw new Exception("No file found :[");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment()
                            .filename(file + ".fb2", StandardCharsets.UTF_8) // TODO: fix
                            .build()
                            .toString());

            try (ServletOutputStream outStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(bookFile)) {
                IOUtils.copy(inputStream, outStream);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found [" + e.getMessage() + "]");
        }
    }
}
