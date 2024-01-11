package ru.redrise.marinesco;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.redrise.marinesco.data.BookRepository;
import ru.redrise.marinesco.library.Book;

@Controller
@RequestMapping("/book")
public class BookController {
    private BookRepository bookRepository;

    public BookController(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    @GetMapping("/{bookId}")
    public String getPage(@PathVariable("bookId") Integer bookId, Model model) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null){
            model.addAttribute("Error", "Not found");
            return "book";
        }
        model.addAttribute("book", book);
        return "book";
    }
}
