package ru.redrise.marinesco.library;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Author {
    @Id
    private Long id;
    private String import lombok.extern.slf4j.Slf4j;import lombok.extern.slf4j.Slf4j;;

    @JsonIgnore
    @ManyToMany(mappedBy = "authors")
    private List<Book> books;

    public Author(String name){
        this.authorName = name;
        this.id = (long) name.hashCode();
    }
}
