package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u " +
            "FROM User AS u")
    Page<User> getAllUsers(Pageable page);

    Page<User> getAllUsersByIdIn(Long[] ids, Pageable page);

    int countAllUsersByName(String name);
}
