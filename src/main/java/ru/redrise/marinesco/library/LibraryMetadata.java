package ru.redrise.marinesco.library;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* collection.info lines:
* 1 - Collection Display Name
* 2 - Collection file name
* 3 - num: 0 - fb2, 1 - non-FB2
* 4 - description
// version.info contains only 1 string
*/
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibraryMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String libraryName;
    private String libraryFileName; // makes no sense, fuck it either way
    private Integer libraryType;    // used to be good idea but still nobody bother to fill it right so ignore this shit
    private String description;

    private String version;

    public void setVersionInfo(String content) throws Exception {
        this.version = content.trim();
    }

    public void setCollectionInfo(String content) throws Exception {
        var lines = content.split("\n");
        if (lines.length < 4)
            throw new Exception("Invalid 'collection.info' file. It contains only "+lines.length+" lines!");
        libraryName = lines[0].trim();
        libraryFileName = lines[1].trim();
        libraryType = Integer.parseInt(lines[2].trim());
        description = lines[3].trim();
    }
}
