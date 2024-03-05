package ru.redrise.marinesco.data;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.library.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>{   
    List<Book> findBySeriesContainingIgnoreCase(String title);
    List<Book> findBySeriesContainingIgnoreCase(String title, PageRequest page);
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByTitleContainingIgnoreCase(String title, PageRequest page);
}