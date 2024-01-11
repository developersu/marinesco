package ru.redrise.marinesco.data;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.library.Author;
import java.util.List;





@Repository
public interface AuthorRepository extends CrudRepository<Author, Long>{  
    Optional<Author> findByAuthorName(String authorName);
    List<Author> findByAuthorNameContainingIgnoreCase(String authorName);
}
