package ru.redrise.marinesco.library;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ru.redrise.marinesco.data.LibraryMetadataRepository;

public class InpxLibraryMetadataScanner {
    private InpxLibraryMetadataScanner() { }

    public static LibraryMetadata saveFromFile(File inpxFile, LibraryMetadataRepository repository) throws Exception {
        var libraryMetadata = new LibraryMetadata();

        try (var zipInputStream = new ZipInputStream(new FileInputStream(inpxFile))) {
            ZipEntry zipEntry;
            
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (isCollection(zipEntry))
                    libraryMetadata.setCollectionInfo(readPlainText(zipInputStream));
                else if (isVersion(zipEntry))
                    libraryMetadata.setVersionInfo(readPlainText(zipInputStream));
            }
        }

        return repository.save(libraryMetadata);
    }

    private static boolean isCollection(ZipEntry zipEntry) {
        return zipEntry.getName().toLowerCase().contains("collection.info");
    }

    private static boolean isVersion(ZipEntry zipEntry){
        return zipEntry.getName().toLowerCase().contains("version.info");
    }

    private static String readPlainText(ZipInputStream zipInputStream) throws Exception {
        var content = new byte[1024];
        var stringBuilder = new StringBuilder();
        while (zipInputStream.read(content) > 0)
            stringBuilder.append(new String(content, StandardCharsets.UTF_8));

        return stringBuilder.toString();
    }
}
