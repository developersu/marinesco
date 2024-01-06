package ru.redrise.marinesco.library;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class InpEntry {
    private List<String> authors = new ArrayList<>();  // Surname,name,by-father
    private List<String> generes = new ArrayList<>();
    private String title;
    private String series;
    private String serNo;
    private String fsFileName;
    private String fileSize; // extracted
    private String libId; // same to filename
    private String deleted; // is deleted flag
    private String fileExtension; // - concatenate to fsFileName
    private LocalDate addedDate;

    private int position;

    public InpEntry(byte[] line) throws Exception{
        // AUTHOR;GENRE;TITLE;SERIES;SERNO;FILE;SIZE;LIBID;DEL;EXT;DATE;
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

        for (String author : authors)
            log.info(author);
        for (String gen : generes)
            log.info(gen);
        
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
    }
     private void parseAuthors(byte[] line) throws Exception{
        for (int i = 0; i < line.length; i++){
            if (line[i] == ':' && i+1 < line.length && line[i+1] == 0x04){
                splitAuthors(new String(line, 0, i, StandardCharsets.UTF_8));
                position = i+1;
                return;
            }
        }

        throw new Exception("Invalid 'inp' file format (parse Authors)");
    }
    private void splitAuthors(String allAuthors){
        authors = Arrays.asList(allAuthors.split(":"));
    }

    private void parseGenere(byte[] line) throws Exception{
        for (int i = position; i < line.length; i++){
            if (line[i] == ':'){
                if (line[i] == ':' && i+1 < line.length && line[i+1] == 0x04){
                    generes.add(new String(line, position, i-position, StandardCharsets.UTF_8));
                    position = i+2;
                    return;
                }
                generes.add(new String(line, position, i-position, StandardCharsets.UTF_8));
                position = i+1;
            }
        }
        throw new Exception("Invalid 'inp' file format (parse Genere)");
    }

    private String parseNextString(byte[] line) throws Exception{
        for (int i = position; i < line.length; i++){
            if (line[i] == 0x04){
                String resultingString = new String(line, position, i-position, StandardCharsets.UTF_8);
                position = i+1;
                return resultingString;
            }
        }

        throw new Exception("Invalid 'inp' file format (parse Title)");
    }
}
