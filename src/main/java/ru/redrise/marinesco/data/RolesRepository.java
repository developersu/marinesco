package ru.redrise.marinesco.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.security.UserRole;
import ru.redrise.marinesco.security.UserRole.Type;


@Repository
public interface RolesRepository extends CrudRepository<UserRole, Long>{   
    List<UserRole> findByType(Type type);
}
