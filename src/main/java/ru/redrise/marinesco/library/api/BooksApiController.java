package ru.redrise.marinesco.library.api;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.redrise.marinesco.data.BookRepository;
import ru.redrise.marinesco.library.Book;

@RestController
@RequestMapping(path = "/api/book", 
                produces = "application/json")
public class BooksApiController {
    private final BookRepository bookRepository ;

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
        var pageRequest = PageRequest.of(page, 10, Sort.by(sortBy).descending());
        return bookRepository.findAll(pageRequest).getContent();
    } 

    @GetMapping("/by/title/{title}")
    public Iterable<Book> getBooksByName(@PathVariable("title") String title,
                                         @RequestParam(value = "page", required = false, defaultValue = "0") Integer page){
        var pageRequest = PageRequest.of(page, 10);
        return bookRepository.findByTitleContainingIgnoreCase(title, pageRequest);
    } 
    
    @GetMapping("/by/series/{series}")
    public Iterable<Book> getBooksBySeries(@PathVariable("series") String series,
                                           @RequestParam(value = "page", required = false, defaultValue = "0") Integer page){
        var pageRequest = PageRequest.of(page, 10);
        return bookRepository.findBySeriesContainingIgnoreCase(series, pageRequest);
    } 
      
    @GetMapping("/by/id/{id}")
    public Book getBooksById(@PathVariable("id") Integer id){
        return bookRepository.findById(id).get();
    } 
}
