package ru.redrise.marinesco.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.library.InpEntry;

@Repository
public interface InpEntryRepository extends CrudRepository<InpEntry, Long>{   
}