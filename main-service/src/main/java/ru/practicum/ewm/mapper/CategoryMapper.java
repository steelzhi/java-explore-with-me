package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.User;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {

    private CategoryMapper() {
    }

    public static Category mapToCategory(NewCategoryDto newCategoryDto) {
        Category category = null;
        if (newCategoryDto != null) {
            category = new Category(
                    0,
                    newCategoryDto.getName()
            );
        }

        return category;
    }

    public static Category mapToCategory(Long catId, CategoryDto categoryDto) {
        Category category = null;
        if (categoryDto != null) {
            category = new Category(
                    catId,
                    categoryDto.getName()
            );
        }

        return category;
    }

    public static CategoryDto mapToCategoryDto(Category category) {
        CategoryDto categoryDto = null;
        if (category != null) {
            categoryDto = new CategoryDto(
                    category.getId(),
                    category.getName()
            );
        }
        return categoryDto;
    }

    public static List<CategoryDto> mapToCategoryDto(List<Category> categories) {
        List<CategoryDto> categoryDtos = new ArrayList<>();
        if (categories != null) {
            for (Category category : categories) {
                categoryDtos.add(mapToCategoryDto(category));
            }
        }
        return categoryDtos;
    }
}