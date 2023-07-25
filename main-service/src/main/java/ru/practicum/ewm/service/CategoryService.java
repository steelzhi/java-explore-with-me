package ru.practicum.ewm.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.dto.UserDto;

import java.util.List;

public interface CategoryService {
    CategoryDto postCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto patchCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long catId);
}
