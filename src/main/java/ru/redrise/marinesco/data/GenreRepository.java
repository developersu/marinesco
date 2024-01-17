package ru.redrise.marinesco.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.library.Genre;


@Repository
public interface GenreRepository extends JpaRepository<Genre, String>{   
}
