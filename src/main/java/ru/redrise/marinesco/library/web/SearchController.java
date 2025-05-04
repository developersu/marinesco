package ru.redrise.marinesco.library.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.data.AuthorRepository;
import ru.redrise.marinesco.data.BookRepository;

@Slf4j
@Controller
@RequestMapping("/search")
public class SearchController {
    
    private final BookRepository booksRepository;
    private final AuthorRepository authorRepository;

    public SearchController(BookRepository bookRepository, AuthorRepository authorRepository){
        this.booksRepository = bookRepository;
        this.authorRepository = authorRepository;
    }
    
    @GetMapping
    public String requestMethodName(@RequestParam String search,
                                    @RequestParam(value = "title", required = false) Boolean title,
                                    @RequestParam(value = "series", required = false) Boolean series,
                                    @RequestParam(value = "author", required = false) Boolean author,
                                    Model model) {
        model.addAttribute("searchPattern", search)
                .addAttribute("isTitle", title)
                .addAttribute("isSeries", series)
                .addAttribute("isAuthor", author);

        if (search.isBlank() || search.length() < 4){
            model.addAttribute("error", "Should be at least 4 chars");
            return "search";
        }

        if (title != null && title)
            model.addAttribute("books", booksRepository.findByTitleContainingIgnoreCase(search));

        if (series != null && series)
            model.addAttribute("series", booksRepository.findBySeriesContainingIgnoreCase(search));

        if (author != null && author)
            model.addAttribute("authors", authorRepository.findByAuthorNameContainingIgnoreCase(search));

        return "search";
    }
}
