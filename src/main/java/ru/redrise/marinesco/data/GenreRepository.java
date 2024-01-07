package ru.redrise.marinesco.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.library.Genre;


@Repository
public interface GenreRepository extends CrudRepository<Genre, String>{   
}
