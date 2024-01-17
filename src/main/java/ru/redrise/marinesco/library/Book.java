package ru.redrise.marinesco.library;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.RainbowDump;

@Slf4j
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Book {
    @Id
    private Integer id;
    private Long libraryId;
    private String libraryVersion;

    @ManyToMany
    private List<Author> authors; // Surname,name,by-father
    @ManyToMany
    private List<Genre> genres;
    private String title;
    private String series;
    private String serNo;
    private String fsFileName; // inside zip
    private Long fileSize; // extracted, in bytes
    private String fileSizeForHumans;
    private String libId; // same to filename
    private String deleted; // is deleted flag
    private String fileExtension; // - concatenate to fsFileName
    private LocalDate addedDate;
    private String container;

    @Transient
    private int position = 0;
    @Transient
    private byte[] line;

    public Book(byte[] line,
            String container,
            Set<Author> authorsCollection,
            Set<Genre> genresCollection,
            Long libraryId,
            String libraryVersion) throws Exception {
        // AUTHOR;GENRE;TITLE;SERIES;SERNO;FILE;SIZE;LIBID;DEL;EXT;DATE;
        this.line = line;
        this.libraryId = libraryId;
        this.libraryVersion = libraryVersion;
        this.id = new String(line).hashCode();
        this.container = container + ".zip";
        this.authors = new ArrayList<>();
        this.genres = new ArrayList<>();
        parseAuthors(authorsCollection);
        parseGenere(genresCollection);
        this.title = parseNextString();
        this.series = parseNextString();
        this.serNo = parseNextString();
        this.fsFileName = parseNextString();
        this.fileSize = Long.valueOf(parseNextString());
        this.fileSizeForHumans = formatByteSize(fileSize);
        this.libId = parseNextString();
        this.deleted = parseNextString();
        this.fileExtension = parseNextString();
        this.addedDate = LocalDate.parse(parseNextString());

        /*
         * for (Author author : authors)
         * log.info(author.getAuthorName());
         * for (Genre gen : genres)
         * log.info(gen.getGenreId());
         * 
         * log.info(title);
         * log.info(series);
         * log.info(serNo);
         * log.info(fsFileName);
         * log.info(fileSize);
         * log.info(libId);
         * log.info(deleted);
         * log.info(fileExtension);
         * log.info(addedDate.toString());
         * 
         * log.info("-----------------");
         * //
         */
    }

    private void parseAuthors(Set<Author> authorsCollection) throws Exception {
        for (; position < line.length; position++) {
            if (line[position] == 0x04) {
                String allAuthors = new String(line, 0, position, StandardCharsets.UTF_8);

                for (String authorName : allAuthors.split(":")) {
                    authorName = authorName.replaceAll(",", " ").trim();
                    if (!authorName.equals("")) {
                        Author author = new Author(authorName);
                        authorsCollection.add(author);
                        authors.add(author);
                    }
                }

                ++position;
                return;
            }
        }
        RainbowDump.hexDumpUTF8(line);
        throw new Exception("Invalid 'inp' file format (parse Authors)");
    }

    private void parseGenere(Set<Genre> genresCollection) throws Exception {
        for (int i = position; i < line.length; i++) {
            if (line[i] == 0x04) {
                String allGenres = new String(line, position, i - position, StandardCharsets.UTF_8);

                for (String genreName : allGenres.split(":")) {
                    Genre genre = new Genre(genreName);
                    genresCollection.add(genre);
                    genres.add(genre);
                }

                position = i + 1;
                return;
            }
        }
        RainbowDump.hexDumpUTF8(line);
        throw new Exception("Invalid 'inp' file format (parse Genere)");
    }

    private String parseNextString() throws Exception {
        for (int i = position; i < line.length; i++) {
            if (line[i] == 0x04) {
                String resultingString = new String(line, position, i - position, StandardCharsets.UTF_8);
                position = i + 1;
                return resultingString;
            }
        }
        RainbowDump.hexDumpUTF8(line);
        throw new Exception("Invalid 'inp' file format (parse Title)");
    }

    private String formatByteSize(double length) {
        final String[] measureName = { "bytes", "KB", "MB", "GB", "TB" };
        int i;
        for (i = 0; length > 1024 && i < measureName.length - 1; i++)
            length = length / 1024;
        return String.format("%,.2f %s", length, measureName[i]);
    }
}
