package ru.redrise.marinesco.library;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Author {
    @Id
    private Long id;
    private String authorName;

    public Author(String name){
        this.authorName = name;
        this.id = (long) name.hashCode();
    }
}
