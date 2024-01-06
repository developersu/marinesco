package ru.redrise.marinesco.library;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConfigurationProperties(prefix = "marinesco.library")
public class InpxScanner {

    private String filesLocation = "";

    private FileSystemResource libraryLocation;

    private LibraryMetadata collectionFileMeta;
    private LibraryMetadata versionFileMeta;

    public void reScan() throws Exception {
        libraryLocation = new FileSystemResource(filesLocation);

        File inpxFile = Stream.of(libraryLocation.getFile().listFiles())
                .filter(file -> file.getName().endsWith(".inpx"))
                .findFirst()
                .get();

        log.info("INPX file found as " + inpxFile.getName());
        
        boolean breaker = false;

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(inpxFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                //log.info("Now parsing: " + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    zipEntry = zipInputStream.getNextEntry();
                    continue;
                }
                /* Lines:
                 * 1 - Collection Display Name
                 * 2 - Collection file name
                 * 3 - num: 0 - fb2, 1 - non-FB2
                 * 4 - description
                 */
                if (zipEntry.getName().toLowerCase().equals("collection.info"))
                    setMetadata("collection.info", zipInputStream);

                // version.info contains only 1 string
                if (zipEntry.getName().toLowerCase().equals("version.info"))
                    setMetadata("version.info", zipInputStream);

                if (zipEntry.getName().toLowerCase().endsWith(".inp")){
                    if (breaker)
                        break;
                    breaker = true;
                    parseInp(zipInputStream, zipEntry.getSize(), zipEntry.getName());
                }

                zipEntry = zipInputStream.getNextEntry();
            }
        }
    }

    private void setMetadata(String filename, ZipInputStream zipInputStream) throws Exception {
        collectionFileMeta = new LibraryMetadata(filename, readPlainText(zipInputStream));
    }

    private String readPlainText(ZipInputStream zipInputStream) throws Exception {
        byte[] content = new byte[1024];
        StringBuilder stringBuilder = new StringBuilder();
        while (zipInputStream.read(content) > 0)
            stringBuilder.append(new String(content, StandardCharsets.UTF_8));

        return stringBuilder.toString();
    }

    private void parseInp(ZipInputStream stream, long fileSize, String fileName) throws Exception {
        ByteBuffer inpByteBuffer = ByteBuffer.allocate((int) fileSize);
        int blockSize = 0x200;
        if (fileSize < 0x200)
            blockSize = (int) fileSize;

        long i = 0;
        byte[] block = new byte[blockSize];

        int actuallyRead;
        while (true) {
            actuallyRead = stream.read(block);
            inpByteBuffer.put(block, 0, actuallyRead);
            i += actuallyRead;
            if ((i + blockSize) > fileSize) {
                blockSize = (int) (fileSize - i);
                if (blockSize == 0)
                    break;
                block = new byte[blockSize];
            }
        }
        new InpFile(inpByteBuffer.array(), fileName);
    }

    public String getFilesLocation() {
        return filesLocation;
    }

    public void setFilesLocation(String location) {
        filesLocation = location;
    }
}
