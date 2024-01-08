package ru.redrise.marinesco.data;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.library.Author;



@Repository
public interface AuthorRepository extends CrudRepository<Author, Long>{  
    Optional<Author> findByAuthorName(String authorName);
}
