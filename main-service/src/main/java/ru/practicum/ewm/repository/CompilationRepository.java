package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c " +
            "FROM Compilation AS c " +
            "WHERE c.pinned = COALESCE(?1, c.pinned)")
    Page<Compilation> getCompilations(Boolean pinned, Pageable page);

}
