package ru.redrise.marinesco.library;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class LibraryMetadata {
    @Id
    private String filename;
    private String content;
}
