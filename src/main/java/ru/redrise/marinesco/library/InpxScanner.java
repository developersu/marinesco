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
import ru.redrise.marinesco.data.AuthorRepository;
import ru.redrise.marinesco.data.GenreRepository;
import ru.redrise.marinesco.data.InpEntryRepository;
import ru.redrise.marinesco.data.LibraryMetadataRepository;

@Slf4j
@Component
@ConfigurationProperties(prefix = "marinesco.library")
public class InpxScanner {

    private String filesLocation = "";

    private LibraryMetadataRepository libraryMetadataRepository;
    private AuthorRepository authorRepository;
    private GenreRepository genreRepository;
    private InpEntryRepository inpEntryRepository;

    public InpxScanner(AuthorRepository authorRepository,
            GenreRepository genreRepository,
            InpEntryRepository inpEntryRepository,
            LibraryMetadataRepository libraryMetadataRepository) {
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.libraryMetadataRepository = libraryMetadataRepository;
    }

    public void reScan() throws Exception {
        LibraryMetadata libraryMetadata = new LibraryMetadata();
        final FileSystemResource libraryLocation = new FileSystemResource(filesLocation);

        File inpxFile = Stream.of(libraryLocation.getFile().listFiles())
                .filter(file -> file.getName().endsWith(".inpx"))
                .findFirst()
                .get();

        log.info("INPX file found as " + inpxFile.getName());

        boolean breaker = false;

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(inpxFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                if (zipEntry.isDirectory()) {
                    zipEntry = zipInputStream.getNextEntry();
                    continue;
                }

                if (zipEntry.getName().toLowerCase().contains("collection.info"))
                    libraryMetadata.setCollectionInfo(readPlainText(zipInputStream));

                else if (zipEntry.getName().toLowerCase().contains("version.info"))
                    libraryMetadata.setVersionInfo(readPlainText(zipInputStream));

                else if (zipEntry.getName().toLowerCase().endsWith(".inp")) {
                    //*
                      if (breaker) {
                      zipEntry = zipInputStream.getNextEntry();
                      continue;
                      }
                      breaker = true;//
                     //*/
                    byte[] content = inpToByteArray(zipInputStream, zipEntry.getSize());
                    parseInpContent(content, zipEntry.getName());
                }

                zipEntry = zipInputStream.getNextEntry();
            }
        }

        libraryMetadataRepository.save(libraryMetadata);
    }

    private String readPlainText(ZipInputStream zipInputStream) throws Exception {
        byte[] content = new byte[1024];
        StringBuilder stringBuilder = new StringBuilder();
        while (zipInputStream.read(content) > 0)
            stringBuilder.append(new String(content, StandardCharsets.UTF_8));

        return stringBuilder.toString();
    }

    private byte[] inpToByteArray(ZipInputStream stream, long fileSize) throws Exception {
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
        return inpByteBuffer.array();
    }

    private void parseInpContent(byte[] content, String name) throws Exception {
        name = name.substring(0, name.lastIndexOf('.'));

        log.info("FILE RELATED " + name);
        int lastIndex = 0;
        for (int i = 0; i < content.length; i++) {
            if (content[i] == '\n') {
                byte[] line = new byte[i - lastIndex];
                System.arraycopy(content, lastIndex, line, 0, i - lastIndex - 1);

                InpEntry book = new InpEntry(line, name, authorRepository, genreRepository);
                //inpEntryRepository.save(book);

                // RainbowDump.hexDumpUTF8(line);

                if (isNextCarriageReturn(i, content)) {
                    i += 2;
                    lastIndex = i;
                } else
                    lastIndex = ++i;
            }
        }
    }

    private boolean isNextCarriageReturn(int i, byte[] content) {
        return i + 1 < content.length && (content[i + 1] == '\r');
    }

    public String getFilesLocation() {
        return filesLocation;
    }

    public void setFilesLocation(String location) {
        filesLocation = location;
    }
}
