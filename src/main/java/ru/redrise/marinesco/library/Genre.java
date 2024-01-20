package ru.redrise.marinesco.library;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Genre {
    @Id
    private String genreId;
    private String humanReadableDescription;

    public Genre(String genreId){
        this.genreId = genreId;
    }
}
