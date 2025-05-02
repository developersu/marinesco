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
    
    private final BookRepository inpEntryRepository;
    private final AuthorRepository authorRepository;

    public SearchController(BookRepository bookRepository, AuthorRepository authorRepository){
        this.inpEntryRepository = bookRepository;
        this.authorRepository = authorRepository;
    }
    
    @GetMapping
    public String requestMethodName(@RequestParam String search, 
        @RequestParam(value = "title", required = false) Boolean title,
        @RequestParam(value = "series", required = false) Boolean series,
        @RequestParam(value = "author", required = false) Boolean author,
        Model model) {

        if (search.trim().isEmpty())
            return "search";
        
        model.addAttribute("searchPattern", search);

        if (search.length() < 4){
            model.addAttribute("error", "Should be at least 4 chars");
            return "search";
        }

        if (title != null){
            List<Book> books = inpEntryRepository.findByTitleContainingIgnoreCase(search);
            if (!books.isEmpty())
                model.addAttribute("books", books);
            model.addAttribute("isTitle", true);
        }
        
        if (series != null){
            List<Book> bookSeries = inpEntryRepository.findBySeriesContainingIgnoreCase(search);
            if (!bookSeries.isEmpty())
                model.addAttribute("series", bookSeries);
            model.addAttribute("isSeries", true);
        }
        
        if (author != null){
            List<Author> authors = authorRepository.findByAuthorNameContainingIgnoreCase(search);
            if (!authors.isEmpty())
                model.addAttribute("authors", authors);
            model.addAttribute("isAuthor", true);
        }

        return "search";
    }
    
}
