package ru.redrise.marinesco.library.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.redrise.marinesco.data.BookRepository;
import ru.redrise.marinesco.library.Book;

@RestController
@RequestMapping(path = "/api/book", 
                produces = "application/json")
public class BooksApiController {
    private BookRepository bookRepository ;

    public BooksApiController(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    @GetMapping(params = "recent")
    //@PreAuthorize("hasRole('ADMIN')") // move to security later
    public Iterable<Book> recentBooks(){
        return getBooks(0, "addedDate");
    }
    
    @GetMapping(params = "page")
    public Iterable<Book> getBooks(@RequestParam(value = "page", required = true) Integer page,
        @RequestParam(value = "sort", required = false, defaultValue = "title") String sortBy){
        PageRequest pageRequest = PageRequest.of(
            page, 10, Sort.by(sortBy).descending());
        
        return bookRepository.findAll(pageRequest).getContent();
    } 

}
