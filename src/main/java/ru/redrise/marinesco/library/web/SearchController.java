package ru.redrise.marinesco.library.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.data.AuthorRepository;
import ru.redrise.marinesco.data.BookRepository;
import ru.redrise.marinesco.library.Author;
import ru.redrise.marinesco.library.Book;

@Slf4j
@Controller
@RequestMapping("/search")
public class SearchController {
    
    private BookRepository inpEntryRepository;
    private AuthorRepository authorRepository;

    public SearchController(BookRepository bookRepository, AuthorRepository authorRepository){
        this.inpEntryRepository = bookRepository;
        this.authorRepository = authorRepository;
    }
    
    @GetMapping
    public String requestMethodName(@RequestParam String search, Model model) {
        
        if (search.trim().equals(""))
            return "search";

        List<Book> books = inpEntryRepository.findByTitleContainingIgnoreCase(search);
        model.addAttribute("books", books);

        List<Book> bookSeries = inpEntryRepository.findBySeriesContainingIgnoreCase(search);
        model.addAttribute("series", bookSeries);
        
        List<Author> authors = authorRepository.findByAuthorNameContainingIgnoreCase(search);
        model.addAttribute("authors", authors);

        return "search";
    }
}
