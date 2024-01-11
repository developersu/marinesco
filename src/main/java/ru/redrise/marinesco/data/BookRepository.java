package ru.redrise.marinesco.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.library.Author;
import ru.redrise.marinesco.library.Book;




@Repository
public interface BookRepository extends CrudRepository<Book, Integer>{   
    List<Book> findBySeriesContainingIgnoreCase(String title);
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findAllByAuthorsContains(Author author);
}