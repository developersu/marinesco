package ru.redrise.marinesco;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.redrise.marinesco.data.AuthorRepository;
import ru.redrise.marinesco.data.BookRepository;
import ru.redrise.marinesco.library.Author;
import ru.redrise.marinesco.library.Book;

@Controller
@RequestMapping("/author")
public class AuthorController {
    private AuthorRepository authorRepository;
    private BookRepository bookRepository;

    public AuthorController(AuthorRepository authorRepository, BookRepository bookRepository){
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping("/{authorId}")
    public String getPage(@PathVariable("authorId") Long authorId, Model model) {
        final Author author = authorRepository.findById(authorId).orElse(null);

        if (author == null){
            model.addAttribute("Error", "Not found");
            return "author";
        }

        List<Book> books = bookRepository.findAllByAuthorsContains(author);

        model.addAttribute("author", author);
        model.addAttribute("books", books);

        return "author";
    }
}
