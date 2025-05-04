package ru.redrise.marinesco.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.library.LibraryMetadata;

@Repository
public interface LibraryMetadataRepository extends CrudRepository<LibraryMetadata, Long>{
}
