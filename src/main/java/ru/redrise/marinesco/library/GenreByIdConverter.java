package ru.redrise.marinesco.library;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import ru.redrise.marinesco.data.GenreRepository;

@Component
public class GenreByIdConverter implements Converter<String, Genre>{

    private final GenreRepository genreRepository;

    public GenreByIdConverter(GenreRepository genreRepository){
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre convert(String id) {
        return genreRepository.findById(id).orElse(null);
    }   
}