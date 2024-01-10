package ru.redrise.marinesco.library;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import ru.redrise.marinesco.data.AuthorRepository;

@Component
public class AthorByIdConverter implements Converter<Long, Author>{

    private AuthorRepository authorRepository;

    public AthorByIdConverter(AuthorRepository authorRepository){
        this.authorRepository = authorRepository;
    }

    @Override
    public Author convert(Long id) {
        return authorRepository.findById(id).orElse(null);
    }   
}