package ru.redrise.marinesco;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.data.InpEntryRepository;
import ru.redrise.marinesco.library.Author;
import ru.redrise.marinesco.library.InpEntry;

@Slf4j
@Controller
@RequestMapping("/book")
public class BookController {
    InpEntryRepository inpEntryRepository;

    public BookController(InpEntryRepository inpEntryRepository){
        this.inpEntryRepository = inpEntryRepository;
    }

    @GetMapping("/{bookId}")
    public String getPage(@PathVariable("bookId") Long bookId, Model model) {
        InpEntry book = inpEntryRepository.findById(bookId).orElse(null);
        if (book == null){
            model.addAttribute("Error", "Not found");
            return "book";
        }
        model.addAttribute("book", book);
        return "book";
    }
}
