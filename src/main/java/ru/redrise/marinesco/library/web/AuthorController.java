package ru.redrise.marinesco.library.web;

import java.util.Comparator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.redrise.marinesco.data.AuthorRepository;
import ru.redrise.marinesco.library.Book;

@Controller
@RequestMapping("/author")
public class AuthorController {
    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository){
        this.authorRepository = authorRepository;
    }

    @GetMapping("/{authorId}")
    public String getPage(@PathVariable("authorId") Long authorId, Model model) {
        var author = authorRepository.findById(authorId).orElse(null);

        if (author == null){
            model.addAttribute("Error", "Not found");
            return "author";
        }

        var books = author.getBooks();
        books.sort(Comparator.comparing(Book::getSeries));

        model.addAttribute("author", author)
             .addAttribute("books", books);

        return "author";
    }
}
