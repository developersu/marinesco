package ru.redrise.marinesco.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.redrise.marinesco.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{
    public User findByUsername(String username);
}
