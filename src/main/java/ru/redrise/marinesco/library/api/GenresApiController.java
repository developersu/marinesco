package ru.redrise.marinesco.library.api;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.redrise.marinesco.data.GenreRepository;
import ru.redrise.marinesco.library.Author;
import ru.redrise.marinesco.library.Genre;

@Slf4j
@RestController
@RequestMapping(path = "/api/genres", produces = "application/json")
public class GenresApiController {
    private GenreRepository genreRepository;

    public GenresApiController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public Iterable<Genre> getGenres(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "sort", required = false, defaultValue = "genreId") String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(sortBy).descending());

        return genreRepository.findAll(pageRequest).getContent();
    }
    
    @GetMapping("/by/id/{id}")
    public Genre getAuthorId(@PathVariable("id") String id){        
        return genreRepository.findById(id).get();
    } 

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Genre postGenre(@RequestBody Genre genre) {
        log.info("{}", genre);
        return genreRepository.save(genre);
    }

    @PutMapping(path = "/{genreId}", consumes = "application/json")
    public Genre putGenre(@PathVariable("genreId") String genreId,
        @RequestBody Genre genre){
            genre.setGenreId(genreId);
        return genreRepository.save(genre);
    }

    @DeleteMapping("/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGenre(@PathVariable("genreId") String genreId){
        try{
            genreRepository.deleteById(genreId);
        }
        catch(EmptyResultDataAccessException ignore){}
    }
}
