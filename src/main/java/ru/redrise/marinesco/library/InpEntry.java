package ru.redrise.marinesco.library;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.RainbowDump;

@Slf4j
@Entity
@Data
public class InpEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToMany
    private List<Author> authors;  // Surname,name,by-father
    @ManyToMany
    private List<Genre> genres;
    private String title;
    private String series;
    private String serNo;
    private String fsFileName; // inside zip
    private String fileSize; // extracted, in bytes
    private String libId; // same to filename
    private String deleted; // is deleted flag
    private String fileExtension; // - concatenate to fsFileName
    private LocalDate addedDate;
    private String container;
    
    @Transient
    private int position;

    public InpEntry(byte[] line, String container) throws Exception{
        // AUTHOR;GENRE;TITLE;SERIES;SERNO;FILE;SIZE;LIBID;DEL;EXT;DATE;
        this.container = container;
        this.authors = new ArrayList<>();
        this.genres = new ArrayList<>();
        parseAuthors(line);
        parseGenere(line);
        this.title = parseNextString(line);
        this.series = parseNextString(line);
        this.serNo = parseNextString(line); 
        this.fsFileName = parseNextString(line); 
        this.fileSize = parseNextString(line); 
        this.libId = parseNextString(line); 
        this.deleted = parseNextString(line); 
        this.fileExtension = parseNextString(line); 
        this.addedDate = LocalDate.parse(parseNextString(line)); 

        /* 
        for (Author author : authors)
            log.info(author.getAuthorName());
        for (Genre gen : genres)
            log.info(gen.getGenreId());
        
        log.info(title);
        log.info(series);
        log.info(serNo);
        log.info(fsFileName);
        log.info(fileSize);
        log.info(libId);
        log.info(deleted);
        log.info(fileExtension);
        log.info(addedDate.toString());

        log.info("-----------------");
        //*/
    }
     private void parseAuthors(byte[] line) throws Exception{
        for (int i = 0; i < line.length; i++){
            if (line[i] == 0x04){
                splitAuthors(new String(line, 0, i, StandardCharsets.UTF_8));
                position = i+1;
                return;
            }
        }

        throw new Exception("Invalid 'inp' file format (parse Authors)");
    }
    private void splitAuthors(String allAuthors){
        for (String author : allAuthors.split(":")){
            authors.add(new Author(author));
        }
    }

    private void parseGenere(byte[] line) throws Exception{
        for (int i = position; i < line.length; i++){
            if (line[i] == 0x04){
                splitGenres(new String(line, 0, i, StandardCharsets.UTF_8));
                position = i+1;
                return;
            }
        }
        RainbowDump.hexDumpUTF8(line);
        throw new Exception("Invalid 'inp' file format (parse Genere)");
    }
    private void splitGenres(String allGenres){
        for (String genre : allGenres.split(":")){
            genres.add(new Genre(genre));
        }
    }

    private String parseNextString(byte[] line) throws Exception{
        for (int i = position; i < line.length; i++){
            if (line[i] == 0x04){
                String resultingString = new String(line, position, i-position, StandardCharsets.UTF_8);
                position = i+1;
                return resultingString;
            }
        }
        RainbowDump.hexDumpUTF8(line);
        throw new Exception("Invalid 'inp' file format (parse Title)");
    }
}
