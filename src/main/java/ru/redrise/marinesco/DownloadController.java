package ru.redrise.marinesco;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.settings.ApplicationSettings;

@Slf4j
@Controller
@RequestMapping("/download")
public class DownloadController {

    private String filesLocation;

    public DownloadController(ApplicationSettings applicationSettings) {
        this.filesLocation = applicationSettings.getFilesLocation();
    }

    @GetMapping(value = "/")
    public void getMethodName(@RequestParam String container,
            @RequestParam String file,
            HttpServletResponse response) throws Exception {

        final FileSystemResource libraryLocation = new FileSystemResource(filesLocation + File.separator + container);
        try (ZipInputStream zipInputStream = new ZipInputStream(libraryLocation.getInputStream())) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                if (zipEntry.getName().contains(file)) {
                    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                    response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                            .filename(file+".fb2", StandardCharsets.UTF_8)  //TODO: fix
                            .build()
                            .toString());
                    ServletOutputStream outStream = response.getOutputStream();
                    sendFile(zipEntry.getSize(), zipInputStream, outStream);
                    outStream.flush();
                    outStream.close();
                    return;
                }

                zipEntry = zipInputStream.getNextEntry();
            }
        }
        throw new Exception("file not found " +
                filesLocation + File.separator + container + " → " + file);
    }

    private void sendFile(long fileSize,
            ZipInputStream zipInputStream,
            ServletOutputStream outStream) throws Exception {

        int blockSize = 0x200;

        if (fileSize < 0x200)
            blockSize = (int) fileSize;

        byte[] block = new byte[blockSize];
        long i = 0;

        while (true) {
            int actuallyRead = zipInputStream.read(block);
            outStream.write(block, 0, actuallyRead);
            i += actuallyRead;
            if ((i + blockSize) > fileSize) {
                blockSize = (int) (fileSize - i);
                if (blockSize == 0)
                    break;
                block = new byte[blockSize];
            }
        }
    }

}
