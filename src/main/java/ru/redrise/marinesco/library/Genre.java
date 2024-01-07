package ru.redrise.marinesco.library;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Genre {
    @Id
    private String genreId;
    private String humanReadableDescription;

    public Genre(String genreId){
        this.genreId = genreId;
    }
}
