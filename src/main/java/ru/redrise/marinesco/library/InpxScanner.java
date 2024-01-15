package ru.redrise.marinesco.library;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.data.AuthorRepository;
import ru.redrise.marinesco.data.BookRepository;
import ru.redrise.marinesco.data.GenreRepository;
import ru.redrise.marinesco.data.LibraryMetadataRepository;
import ru.redrise.marinesco.settings.ApplicationSettings;

@Slf4j
@Component
public class InpxScanner implements Runnable {

    private static volatile Thread parser;
    private static volatile String lastRunErrors;

    private LibraryMetadata libraryMetadata;
    private LibraryMetadataRepository libraryMetadataRepository;
    private AuthorRepository authorRepository;
    private GenreRepository genreRepository;
    private BookRepository bookRepository;

    private String filesLocation;

    public InpxScanner(ApplicationSettings applicationSettings,
            AuthorRepository authorRepository,
            GenreRepository genreRepository,
            BookRepository bookRepository,
            LibraryMetadataRepository libraryMetadataRepository) {
        this.filesLocation = applicationSettings.getFilesLocation();
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
        this.libraryMetadataRepository = libraryMetadataRepository;
    }

    /*
     * @return true if executed, false if already running
     */
    public boolean reScan() {
        if (parser == null || !parser.isAlive()) {
            parser = new Thread(this);
            parser.start();
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        try {
            final FileSystemResource libraryLocation = new FileSystemResource(filesLocation);

            final File inpxFile = Stream.of(libraryLocation.getFile().listFiles())
                    .filter(file -> file.getName().endsWith(".inpx"))
                    .findFirst()
                    .get();

            log.debug("INPX file found as " + inpxFile.getName());

            getLibraryMetadata(inpxFile);
            parseInp(inpxFile);
            // Once multiple libraries imlemented, add here 'delete recrodds with old
            // version of the library'
            // TODO: fix lirary ID changes on every update: add selector on the front
        } catch (Exception e) {
            log.error("{}", e);
            InpxScanner.lastRunErrors = e.getMessage();
        }
    }

    private void getLibraryMetadata(File inpxFile) throws Exception {
        libraryMetadata = new LibraryMetadata();

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(inpxFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                if (zipEntry.getName().toLowerCase().contains("collection.info"))
                    libraryMetadata.setCollectionInfo(readPlainText(zipInputStream));

                else if (zipEntry.getName().toLowerCase().contains("version.info"))
                    libraryMetadata.setVersionInfo(readPlainText(zipInputStream));

                zipEntry = zipInputStream.getNextEntry();
            }
        }

        libraryMetadata = libraryMetadataRepository.save(libraryMetadata);
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

    private void parseInp(File inpxFile) throws Exception {
        /*
        log.warn("REMOVE TEMPORARY SOLUTION - BREAKER");
        log.warn("REMOVE TEMPORARY SOLUTION - BREAKER");
        log.warn("REMOVE TEMPORARY SOLUTION - BREAKER");
        boolean breaker = false;
        */
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(inpxFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                if (zipEntry.getName().toLowerCase().endsWith(".inp")) {
                    /*
                    if (breaker) {
                        zipEntry = zipInputStream.getNextEntry();
                        continue;
                    }
                    breaker = true;
                    // */
                    byte[] content = inpToByteArray(zipInputStream, zipEntry.getSize());
                    parseInpContent(content, zipEntry.getName());
                }
                zipEntry = zipInputStream.getNextEntry();
            }
        }
    }

    private void parseInpContent(byte[] content, String name) throws Exception {
        name = name.substring(0, name.lastIndexOf('.'));

        log.info("FILE RELATED " + name);
        int lastIndex = 0;
        for (int i = 0; i < content.length; i++) {
            if (content[i] == '\n') {
                byte[] line = new byte[i - lastIndex];
                System.arraycopy(content, lastIndex, line, 0, i - lastIndex - 1);

                Book book = new Book(line,
                        name,
                        authorRepository,
                        genreRepository,
                        libraryMetadata.getId(),
                        libraryMetadata.getVersion());

                bookRepository.save(book);

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

    public static String getLastRunErrors() {
        return lastRunErrors;
    }
}
