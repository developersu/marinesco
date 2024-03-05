package ru.redrise.marinesco.library.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.redrise.marinesco.data.AuthorRepository;
import ru.redrise.marinesco.library.Author;

@RestController
@RequestMapping(path = "/api/author", 
                produces = "application/json")
public class AuthorsApiController {
    private AuthorRepository authorRepository;

    public AuthorsApiController(AuthorRepository authorRepository){
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public Iterable<Author> getAuthors(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "sort", required = false, defaultValue = "authorName") String sortBy){
        PageRequest pageRequest = PageRequest.of(
            page, 10, Sort.by(sortBy).descending());
        
        return authorRepository.findAll(pageRequest).getContent();
    } 
    
    @GetMapping("/by/name/{name}")
    public Iterable<Author> getAuthorId(
        @PathVariable("name") String authorName,
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page){        
        
        PageRequest pageRequest = PageRequest.of(page, 10);

        return authorRepository.findByAuthorNameContainingIgnoreCase(authorName, pageRequest);
    } 

    @GetMapping("/by/id/{id}")
    public Author getAuthorId(@PathVariable("id") Long authorId){        
    
        return authorRepository.findById(authorId).get();
    } 
}
