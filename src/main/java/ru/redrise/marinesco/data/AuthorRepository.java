package ru.redrise.marinesco.data;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.library.Author;
import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>{  
    Optional<Author> findByAuthorName(String authorName);
    List<Author> findByAuthorNameContainingIgnoreCase(String authorName);
    List<Author> findByAuthorNameContainingIgnoreCase(String authorName, PageRequest pageRequest);
}
