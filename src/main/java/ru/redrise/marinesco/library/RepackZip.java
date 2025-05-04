package ru.redrise.marinesco.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.settings.ApplicationSettings;

/**
 * Opens zip file with book files in them, creates folder with name of zip file,
 * extracts every book file and put it into individual zip archive
 */
@Slf4j
@Component
public class RepackZip {
    private final ThreadPoolTaskExecutor executor;
    private final File folderContainer;
    private String lastTimeExec = "";
    

    public RepackZip(ThreadPoolTaskExecutor executor, ApplicationSettings applicationSettings) {
        this.executor = executor;
        this.folderContainer = new File(applicationSettings.getFilesLocation());
    }

    public boolean repack() {
        if (executor.getActiveCount() > 0)
            return false;

        lastTimeExec =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("DD.MM.YYYY HH:mm:ss"));
        /*
         * if (lastTimeExec != null)
            return true;
         */
        new Thread(() -> Stream.of(folderContainer.listFiles())
                .filter(file -> file.getName().toLowerCase().endsWith(".zip"))
                .forEach(file -> executor.execute(new InnerRepackZip(file)))
        ).start();

        return true;
    }

    private class InnerRepackZip implements Runnable {
        private String dirLocation;
        private final File container;

        private InnerRepackZip(File container) {
            this.container = container;
        }

        @Override
        public void run() {
            try (var zipInputStream = new ZipInputStream(new FileInputStream(container))) {
                dirLocation = container.getParentFile().getAbsolutePath() +
                        File.separator +
                        container.getName().replaceAll("(\\.zip)|(\\.ZIP)", "");

                new File(dirLocation).mkdirs();

                if (!new File(dirLocation).exists())
                    throw new Exception("Dir does not created / exists");

                ZipEntry zipEntry;

                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    if (!zipEntry.isDirectory())
                        makeZip(zipEntry, zipInputStream);
                }

            } catch (Exception e) {
                log.error(e.toString());
            }
            log.info("Complete: {}", container.getName());
        }

        private void makeZip(ZipEntry entry, InputStream inputStream) throws Exception {
            final String newZipFileLocation = dirLocation + File.separator + entry.getName() + ".zip";
            final File newZipFile = new File(newZipFileLocation);

            if (newZipFile.exists()) {
                log.info("Skipping unpacked: {}", newZipFileLocation);
                return;
            }

            try (var outputStream = new ZipOutputStream(new FileOutputStream(newZipFile))) {
                var entryToCompress = new ZipEntry(entry.getName());
                outputStream.putNextEntry(entryToCompress);

                long fileSize = entry.getSize();
                int blockSize = 2048;

                if (fileSize < 2048)
                    blockSize = (int) fileSize;

                byte[] block = new byte[blockSize];
                long i = 0;

                while (true) {
                    int actuallyRead = inputStream.read(block);
                    outputStream.write(block, 0, actuallyRead);
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
    }

    public String getLastExecTime(){
        return lastTimeExec;
    }
}