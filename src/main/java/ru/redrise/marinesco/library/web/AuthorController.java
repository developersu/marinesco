package ru.redrise.marinesco.library.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.comparator.Comparators;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.redrise.marinesco.data.AuthorRepository;
import ru.redrise.marinesco.library.Author;
import ru.redrise.marinesco.library.Book;

@Controller
@RequestMapping("/author")
public class AuthorController {
    private AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository){
        this.authorRepository = authorRepository;
    }

    @GetMapping("/{authorId}")
    public String getPage(@PathVariable("authorId") Long authorId, Model model) {
        final Author author = authorRepository.findById(authorId).orElse(null);

        if (author == null){
            model.addAttribute("Error", "Not found");
            return "author";
        }

        List<Book> books = author.getBooks();

        Collections.sort(books, (a, b) -> a.getSeries().compareTo(b.getSeries()));

        model.addAttribute("author", author);
        model.addAttribute("books", books);

        return "author";
    }
}
