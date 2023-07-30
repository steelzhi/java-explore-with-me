package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c " +
            "FROM Category AS c")
    Page<Category> getAllCategories(Pageable page);

    Category findCategoryByName(String name);
}
