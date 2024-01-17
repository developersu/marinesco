package ru.redrise.marinesco.library;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.data.AuthorRepository;
import ru.redrise.marinesco.data.BookRepository;
import ru.redrise.marinesco.data.GenreRepository;
import ru.redrise.marinesco.data.LibraryMetadataRepository;
import ru.redrise.marinesco.settings.ApplicationSettings;

@Slf4j
@Component
public class InpxScanner {
    private static volatile String lastRunErrors = "";
    private static LocalDateTime lastRunTime = LocalDateTime.of(1970, 01, 01, 0, 0, 0);

    private TaskExecutor executor;
    private LibraryMetadataRepository libraryMetadataRepository;
    private AuthorRepository authorRepository;
    private GenreRepository genreRepository;
    private BookRepository bookRepository;

    private String filesLocation;

    public InpxScanner(TaskExecutor executor,
            ApplicationSettings applicationSettings,
            AuthorRepository authorRepository,
            GenreRepository genreRepository,
            BookRepository bookRepository,
            LibraryMetadataRepository libraryMetadataRepository) {
        this.executor = executor;
        this.filesLocation = applicationSettings.getFilesLocation();
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
        this.libraryMetadataRepository = libraryMetadataRepository;
    }

    /*
     * @return true if executed, false otherwise
     */
    public boolean reScan() {

        LocalDateTime currentDateTime = LocalDateTime.now();

        if (ChronoUnit.MINUTES.between(lastRunTime, currentDateTime) < 5) {
            lastRunErrors = "Too frequent requests. Please whait 5 min. Last attmpt: "
                    + lastRunTime.format(DateTimeFormatter.ofPattern("DD.MM.YYYY HH:mm:ss"));
            return false;
        }
        lastRunTime = currentDateTime;
        lastRunErrors = "";

        Thread scanThread = new Thread(() -> {
            try {
                File inpxFile = getInpxFile();
                log.debug("INPX file found: " + inpxFile.getName());

                LibraryMetadata libMetadata = InpxLibraryMetadataScanner.saveFromFile(inpxFile,
                        libraryMetadataRepository);

                Long libId = libMetadata.getId();
                String libVersion = libMetadata.getVersion();
                HashMap<String, byte[]> inpEntries = collectInp(inpxFile);

                for (Map.Entry<String, byte[]> entry : inpEntries.entrySet())
                    executor.execute(new InpxWorker(entry, libId, libVersion));
            } catch (Exception e) {
                log.error("{}", e);
                lastRunErrors = lastRunErrors + " " + e.getMessage();
            }
        });

        scanThread.start();

        return true;
    }

    private File getInpxFile() throws Exception {
        final FileSystemResource libraryLocation = new FileSystemResource(filesLocation);
        return Stream.of(libraryLocation.getFile().listFiles())
                .filter(file -> file.getName().endsWith(".inpx"))
                .findFirst()
                .get();
    }

    private HashMap<String, byte[]> collectInp(File inpxFile) throws Exception {
        final HashMap<String, byte[]> inpEntries = new HashMap<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(inpxFile))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (isInp(zipEntry)) {
                    String zipEntryName = zipEntry.getName();
                    zipEntryName = zipEntryName.substring(0, zipEntryName.lastIndexOf('.'));
                    inpEntries.put(zipEntryName, inpToByteArray(zipInputStream, zipEntry.getSize()));
                }
            }
        }
        return inpEntries;
    }

    private boolean isInp(ZipEntry zipEntry) {
        return zipEntry.getName().toLowerCase().endsWith(".inp");
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

    private boolean isNextCarriageReturn(int i, byte[] content) {
        return i + 1 < content.length && (content[i + 1] == '\r');
    }

    public static String getLastRunErrors() {
        return lastRunErrors;
    }

    private class InpxWorker implements Runnable {

        private Long libraryId;
        private String libraryVersion;
        private String name;
        private byte[] content;

        private InpxWorker(Map.Entry<String, byte[]> entry,
                Long libraryId,
                String libraryVersion) {
            this.libraryId = libraryId;
            this.libraryVersion = libraryVersion;
            this.name = entry.getKey();
            this.content = entry.getValue();
        }

        @Override
        public void run() {
            final List<Book> books = new ArrayList<>();
            final Set<Author> authors = new HashSet<>();
            final Set<Genre> genres = new HashSet<>();
            try {
                log.info("FILE RELATED " + name);

                int lastIndex = 0;
                for (int i = 0; i < content.length; i++) {
                    if (content[i] == '\n') {
                        byte[] line = new byte[i - lastIndex];
                        System.arraycopy(content, lastIndex, line, 0, i - lastIndex - 1);

                        books.add(new Book(line,
                                name,
                                authors,
                                genres,
                                libraryId,
                                libraryVersion));

                        if (isNextCarriageReturn(i, content)) {
                            i += 2;
                            lastIndex = i;
                        } else
                            lastIndex = ++i;
                    }
                }
                saveAll(books, authors, genres);
            } catch (Exception e) {
                log.error("{}", e);
                lastRunErrors = lastRunErrors + " " + e.getMessage();
            }
        }
    }

    /* REMINDER: DO NOT PUT THIS SHIT INTO THREAD */
    private synchronized void saveAll(List<Book> books, Set<Author> authors, Set<Genre> genres) {
        authorRepository.saveAll(authors);
        genreRepository.saveAll(genres);
        bookRepository.saveAll(books);
    }
}
